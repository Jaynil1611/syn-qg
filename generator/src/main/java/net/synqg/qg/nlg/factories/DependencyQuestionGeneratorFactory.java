package net.synqg.qg.nlg.factories;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.synqg.qg.nlg.qgtemplates.QgTemplate;
import net.synqg.qg.nlg.qgtemplates.depqgtemplates.AbstractDepQgTemplate;
import net.synqg.qg.nlg.qgtemplates.depqgtemplates.AttrQgTemplate;
import net.synqg.qg.nlg.qgtemplates.depqgtemplates.CcompQgTemplate;
import net.synqg.qg.nlg.qgtemplates.depqgtemplates.DobjQgTemplate;
import net.synqg.qg.nlg.qgtemplates.depqgtemplates.IobjQgTemplate;
import net.synqg.qg.nlg.qgtemplates.depqgtemplates.PcompQgTemplate;
import net.synqg.qg.nlg.qgtemplates.depqgtemplates.PobjTemplate;
import net.synqg.qg.nlg.qgtemplates.depqgtemplates.XcompQgTemplate;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.SemanticRoleList;
import net.synqg.qg.nlp.labels.DependencyLabel;
import net.synqg.qg.service.IQgAnalysis;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author viswa
 */
public class DependencyQuestionGeneratorFactory extends AbstractQuestionGeneratorFactory {

    @Override
    public List<QgTemplate> extractTemplate(IQgAnalysis qgAnalysis) {

        List<QgTemplate> qgTemplates = new ArrayList<>();
        List<DependencyNode> depNodes = qgAnalysis.getSentenceParse().dependencyNodes();
        List<SemanticRoleList> srlSpans = qgAnalysis.getSrlSpans();
        // go through all the dep-nodes which have a head
        for (DependencyNode depNode : depNodes) {

            if (depNode.head() == null) {
                continue;
            }

            List<SemanticRoleList> srlSpansOfThisVerb = srlSpans.stream()
                    .filter(srl ->
                            (srlAndParentOfDepNodeMatch(depNode, srl)
                                    || (depNode.head().depLabel() == DependencyLabel.PREP && srlAndGrandParentofDepNodeMatch(depNode, srl))))
                    .collect(Collectors.toList());

            for (SemanticRoleList srl : srlSpansOfThisVerb) {
                AbstractDepQgTemplate qgTemplate;
                switch ((depNode.depLabel())) {
                    case XCOMP:
                        qgTemplate = new XcompQgTemplate();
                        break;
                    case CCOMP:
                        qgTemplate = new CcompQgTemplate();
                        break;
                    case DOBJ:
                        // if the current dobj's verb has an xcomp parent, then the parent must be +ve or -ve else
                        // questions cannot be directly inferred.
                        // TODO: move this to the parsing side.
                        boolean entailedPolarity = implicationService.computeEntailedPolarityOrDefault(depNode
                                        .head(),// this is the preposition
                                true); // this is the verb
                        qgTemplate = new DobjQgTemplate(entailedPolarity);
                        break;
                    case OBJ:
                        qgTemplate = new DobjQgTemplate(true);
                        break;
                    case POBJ:
                        boolean entailedPolarity1 = implicationService.computeEntailedPolarityOrDefault(depNode
                                .head() // this is the preposition
                                .head(), true); // this is the verb
                        qgTemplate = new PobjTemplate(entailedPolarity1);
                        break;
                    case ADVCL:
                        qgTemplate = new PcompQgTemplate();
                        break;
                    case IOBJ:
                        if (implicationService.computeEntailedPolarityOrDefault(depNode.head(),true)) {
                            qgTemplate = new IobjQgTemplate();
                        } else {
                            qgTemplate = null;
                        }
                        break;
                    case COP:
                        qgTemplate = new AttrQgTemplate();
                        break;
                    default:
                        qgTemplate = null;
                }
                if (qgTemplate != null) {

                    String hypernym = null;
                    qgTemplate.trigger(depNode.form() + "::" + depNode.depLabel());
                    try {
                        hypernym = getHypernyms(depNode.form(), depNode.pos().toString(),
                                qgAnalysis.getSentenceParse().sentenceText());
                        if (hypernym.contains("<!DOCTYPE HTML PUBLIC")) {
                            hypernym = null;
                        }
                        Gson gson = new Gson();
                        WordNetResponse result = gson.fromJson(hypernym, WordNetResponse.class);
                        hypernym = result.hypernym;
                    } catch (IOException | NullPointerException e) {
                        //System.out.println("Could not get the WordNet Sense for " + depNode.form());
                    }
                    // TODO: need to clean this up.
                    // extract A1 if the depNode is a part of it, else extract A2.
                    Optional<String> dobj;
                    Optional<String> pobj;
                    if (depNode.depLabel() == DependencyLabel.POBJ) {
                        pobj = getObjectPhrase(srl, depNode.form(), true);
                        ImmutableList<DependencyNode> dobjNodes = depNode.head() // the preposition
                                .head() // the main root verb
                                .childrenWithDepLabel(DependencyLabel.DOBJ); // the dobj child of this root verb.
                        dobj = dobjNodes.isEmpty() ? Optional.empty() : getObjectPhrase(srl, dobjNodes.get(0).form(), false);
                        if (pobj.isPresent()) {
                            // extract subject from A0
                            qgTemplate.subject(getSubject(srl, qgAnalysis.getSentenceParse()));
                            qgTemplate.dObject(dobj.isPresent() ? dobj.get().trim() : "");
                            qgTemplate.pObject(pobj.get());
                            if (/*depNode.depLabel() == DependencyLabel.PCOMP ||*/ depNode.depLabel() == DependencyLabel.ADVCL ||
                                    depNode.depLabel() == DependencyLabel.POBJ) {
                                qgTemplate.extraField(depNode.head().form());
                            } else {
                                //include all SRL arguments except for the argument which has the answer-node and the subject
                                qgTemplate.extraField(getExtraFields(srl, new ArrayList<>(), dobj.get()));
                            }
                            // get the effective verb = including all the modals, auxillaries and negations
                            qgTemplate.verb(getVerb(srl));
                            qgTemplate.namedEntityType(depNode.namedEntityType());
                            qgTemplate.hypernym(hypernym);
                            qgTemplates.add(qgTemplate);

                        }
                    } else {
                        dobj = getObjectPhrase(srl, depNode.form(), false);
                        if (dobj.isPresent()) {
                            // extract subject from A0
                            qgTemplate.subject(getSubject(srl, qgAnalysis.getSentenceParse()));
                            qgTemplate.dObject(dobj.get().trim());
                            if (/*depNode.depLabel() == DependencyLabel.PCOMP ||*/ depNode.depLabel() == DependencyLabel.ADVCL ||
                                    depNode.depLabel() == DependencyLabel.POBJ) {
                                qgTemplate.extraField(depNode.head().form());
                            } else {
                                //include all SRL arguments except for the argument which has the answer-node and the subject
                                qgTemplate.extraField(getExtraFields(srl, new ArrayList<>(), dobj.get()));

                            }
                            // get the effective verb = including all the modals, auxillaries and negations
                            qgTemplate.verb(getVerb(srl));
                            qgTemplate.namedEntityType(depNode.namedEntityType());
                            qgTemplate.hypernym(hypernym);
                            qgTemplates.add(qgTemplate);

                        }
                    }
                }
            }
        }
        return qgTemplates;
    }

    /**
     * Is the head of this dependency node pointing to the same verb as the SRL's predicate.
     *
     * @param node          "the node whose parent (verb) needs to be checked"
     * @param semanticRoles "the SRL whose verb should match"
     * @return matched/not
     */
    private boolean srlAndParentOfDepNodeMatch(DependencyNode node, SemanticRoleList semanticRoles) {
        return semanticRoles.verb().equals(node.head());
    }

    /**
     * Is the head of this dependency node pointing to the same verb as the SRL's predicate.
     *
     * @param node          "the node whose parent's parent (verb) needs to be checked"
     * @param semanticRoles "the SRL whose verb should match"
     * @return matched/not
     */
    private boolean srlAndGrandParentofDepNodeMatch(DependencyNode node, SemanticRoleList semanticRoles) {
        return srlAndParentOfDepNodeMatch(node.head(), semanticRoles);
    }

    private String getHypernyms(String object, String pos, String text) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8001/predictwsd");

        String json = "{\"sentence\":\"" + text + "\"," + "\"word\":\"" + object + "\"}";
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));
        String output = br.lines().collect(Collectors.joining());
        client.close();
        return output;

    }

    private class WordNetResponse {
        @SerializedName("hypernym")
        String hypernym;
    }

}