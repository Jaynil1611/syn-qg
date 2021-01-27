package net.synqg.qg.service;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.NamedEntitySpan;
import net.synqg.qg.nlp.SemanticRole;
import net.synqg.qg.nlp.SemanticRoleList;
import net.synqg.qg.nlp.SentenceParse;
import net.synqg.qg.nlp.labels.DependencyLabel;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.nlp.labels.PosLabel;
import net.synqg.qg.nlp.labels.SemanticRoleLabel;
import net.synqg.qg.nlp.normalizer.TextNormalizer;
import net.synqg.qg.nlp.jsonresponse.DependencyResponse;
import net.synqg.qg.nlp.jsonresponse.LemmaResponse;
import net.synqg.qg.nlp.jsonresponse.NERResponse;
import net.synqg.qg.nlp.jsonresponse.SemanticResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author viswa
 */
@Slf4j
public class DefaultQgAnalysisService implements QgAnalysisService {

    static final List<String> verbsToDiscard = new ArrayList<>(Arrays.asList("believe", "thought"));
    private static Function<String, String> normalizer;
    private boolean printLogs = false;

    public DefaultQgAnalysisService() {
        normalizer = new TextNormalizer();
    }

    public DefaultQgAnalysisService(boolean printLogs) {
        this.printLogs = printLogs;
        normalizer = new TextNormalizer();
    }

    public List<IQgAnalysis> parse(List<String> sentences) {
        return sentences
                .parallelStream()
                .map(normalizer)
                .map(this::parse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private IQgAnalysis parse(String text) {
        QgAnalysis qgAnalysis = new QgAnalysis();

        try {
            String ner = getJson(text, "predictner");
            String fineNer = getJson(text, "predictfinener");
            SentenceParse sentenceParse = new SentenceParse()
                    .sentenceText(text)
                    .dependencyNodes(buildDependecyNodes(getJson(text, "predictdep"), ner,
                            getJson(text, "predictlemma")));
            String predictSrl = getJson(text, "predictsrl");
            List<SemanticRoleList> spanListList = getSemanticRoles(predictSrl, sentenceParse.dependencyNodes());
            qgAnalysis.srlSpans(spanListList);
            qgAnalysis.namedEntities(buildNamedEntityList(ner, sentenceParse));
            qgAnalysis.sentenceParse(sentenceParse);
            qgAnalysis.fineGrainedNamedEntities(buildFineGrainedNamedEntityList(fineNer, sentenceParse));

            Set<NamedEntitySpan> namedEntitySpans = new HashSet<>();
            namedEntitySpans.addAll(buildNamedEntitySpans(ner, sentenceParse));
            namedEntitySpans.addAll(buildNamedEntitySpans(fineNer, sentenceParse));
            qgAnalysis.namedEntitySpans(namedEntitySpans);

            return qgAnalysis;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    public static String getJson(String input, String apitype) throws ClientProtocolException, IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8001/" + apitype);
        Gson gson = new Gson();
        JSONObject json = new JSONObject();
        if (apitype.equalsIgnoreCase("predictcoref")) {
            json.put("document", input);
        } else if (apitype.equalsIgnoreCase("nounify")) {
            json.put("word", input);
        } else {
            json.put("sentence", input);
        }
        StringEntity entity = new StringEntity(json.toJSONString());
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

    private static List<String> getAllMatches(String text, String regex) {
        List<String> matches = new ArrayList<String>();
        for (int length = 1; length <= text.length(); length++) {
            for (int index = 0; index <= text.length() - length; index++) {
                String sub = text.substring(index, index + length);
                if (sub.matches(regex) && !sub.substring(1).contains("[")) {
                    matches.add(sub);
                }
            }
        }
        return matches;
    }

    private List<Map<SemanticRoleLabel, String>> semanticRoleLabelFilters(List<Map<SemanticRoleLabel, String>> semanticRoles) {

        List<String> filterSrlString = new ArrayList<>();

        List<Map<SemanticRoleLabel, String>> newSrlList = new ArrayList<>();

        for (Map<SemanticRoleLabel, String> semanticRoleLabelStringMap : semanticRoles) {
            if (semanticRoleLabelStringMap.containsKey(SemanticRoleLabel.V)) {
                for (String verbTodiscard : verbsToDiscard) {
                    if (semanticRoleLabelStringMap.get(SemanticRoleLabel.V).toLowerCase().contains(verbTodiscard)) {
                        if (semanticRoleLabelStringMap.containsKey(SemanticRoleLabel.ARG1)) {
                            filterSrlString.add(semanticRoleLabelStringMap.get(SemanticRoleLabel.ARG1));
                        }
                        if (semanticRoleLabelStringMap.containsKey(SemanticRoleLabel.ARG2)) {
                            filterSrlString.add(semanticRoleLabelStringMap.get(SemanticRoleLabel.ARG2));
                        }
                    }
                }
            }
        }

        for (Map<SemanticRoleLabel, String> semanticRoleLabelStringMap : semanticRoles) {

            String sentence = "";

            if (!semanticRoleLabelStringMap.containsKey(SemanticRoleLabel.ARG0)
                    || !semanticRoleLabelStringMap.containsKey(SemanticRoleLabel.V)) {
                newSrlList.add(semanticRoleLabelStringMap);
                continue;
            }

            sentence = semanticRoleLabelStringMap.get(SemanticRoleLabel.ARG0) +
                    semanticRoleLabelStringMap.get(SemanticRoleLabel.V);

            if (semanticRoleLabelStringMap.containsKey(SemanticRoleLabel.ARG1)) {
                sentence = sentence + semanticRoleLabelStringMap.get(SemanticRoleLabel.ARG1);

                for (String srlString : filterSrlString) {
                    if (!srlString.contains(sentence)) {
                        newSrlList.add(semanticRoleLabelStringMap);

                    }

                }

            }

            if (!semanticRoleLabelStringMap.containsKey(SemanticRoleLabel.ARG1) && semanticRoleLabelStringMap.containsKey(SemanticRoleLabel.ARG2)) {
                sentence = sentence + " " + semanticRoleLabelStringMap.get(SemanticRoleLabel.ARG2);

                for (String srlString : filterSrlString) {
                    if (!srlString.contains(sentence)) {
                        newSrlList.add(semanticRoleLabelStringMap);
                    }
                }
            }
        }
        return newSrlList;
    }

    private List<SemanticRoleList> getSemanticRoles(String input, List<DependencyNode> dependencyNodes) {
        Gson gson = new Gson();
        SemanticResponse result = gson.fromJson(input, SemanticResponse.class);
        List<SemanticRoleList> semanticRoles = new ArrayList<>();
        for (SemanticResponse.Verb verb : result.verbs()) {
            String[] tags = verb.tags();
            SemanticRoleList spanList = new SemanticRoleList();
            SemanticRole span = null;
            if (tags.length != dependencyNodes.size()) {
                System.out.println("SRL and Dependency have been tokenized separately. The number of tokens do not match. ");
            }
            for (int i = 0; i < tags.length; i++) {
                String tag = tags[i];
                DependencyNode token = dependencyNodes.get(i);
                if (tag.startsWith("B-")) {
                    if (span != null) {
                        spanList.add(span);
                    }
                    if (tag.equalsIgnoreCase("B-V")) {
                        spanList.verb(dependencyNodes.get(i));
                    }
                    span = new SemanticRole();
                    span.type(SemanticRoleLabel.fromString(tag.replace("B-", "")));
                    span.tokens().add(token);
                } else if (tag.startsWith("I-")) {
                    span.tokens().add(token);
                } else if (tag.equalsIgnoreCase("O") && span != null) {
                    spanList.add(span);
                    span = null;
                    // when there is no tag, don't create any span
                }
                if (i == (tags.length - 1) && span != null) { // add the last span
                    spanList.add(span);
                }
            }
            semanticRoles.add(spanList);
        }
        return semanticRoles;
    }

    /**
     * This makes an assumption that the tokens in the spans coming out of the SRL predictor are space concatenated.
     *
     * @param spanString
     * @param dependencyNodes
     * @return
     */
    private List<DependencyNode> getTokens(String spanString, int spanStart, List<DependencyNode> dependencyNodes) {
        String[] tokens = spanString.split(" ");
        int spanEnd = tokens.length;
        return dependencyNodes.subList(spanStart, spanEnd);
    }

    private NamedEntityType getNamedEntitytype(String s) {

        if (s.equals("U-PER") || s.equalsIgnoreCase("B-PER") || s.equalsIgnoreCase("L-PER")) {
            return NamedEntityType.PERSON;
        }

        if (s.equals("U-ORG")) {
            return NamedEntityType.ORGANIZATION;
        }

        return NamedEntityType.MISC;

    }

    private PosLabel getPos(String s) {
        return PosLabel.fromString(s);
    }

    private ImmutableList<DependencyNode> buildDependecyNodes(String depinput, String nerinput, String lemmainput) {

        List<DependencyNode> dependencyNodes = new ArrayList<>();

        Gson gson = new Gson();
        DependencyResponse dependencyResponse = gson.fromJson(depinput, DependencyResponse.class);
        LemmaResponse lemmaResponse = gson.fromJson(lemmainput, LemmaResponse.class);
        NERResponse nerResponse = gson.fromJson(nerinput, NERResponse.class);

        List<String> depLabels = dependencyResponse.depLabels();
        List<String> words = dependencyResponse.words();
        List<Integer> heads = dependencyResponse.nodeHeads();
        List<String> pos = dependencyResponse.posLabels();
        List<String> lemmas = lemmaResponse.lemmas();
        List<String> ners = nerResponse.tags();

        int numberOfNodes = depLabels.size();
        if (words.size() != numberOfNodes || heads.size() != numberOfNodes || pos.size() != numberOfNodes
                || lemmas.size() != numberOfNodes || ners.size() != numberOfNodes) {
            log.error("All parsers have not returned the same size of tokens. (One reason could be the number of extra spaces.)");
        }

        for (int i = 0; i < numberOfNodes; i++) {

            DependencyNode dependencyNode = new DependencyNode();

            dependencyNode.form(words.get(i));
            dependencyNode.depLabel(DependencyLabel.fromString(depLabels.get(i)));
            dependencyNode.lemma(lemmas.get(i));
            dependencyNode.namedEntityType(NamedEntityType.fromString(ners.get(i)));
            dependencyNode.pos(getPos(pos.get(i)));

            dependencyNodes.add(dependencyNode);
        }

        for (int i = 0; i < depLabels.size(); i++) {

            DependencyNode dependencyNode = dependencyNodes.get(i);
            if (heads.get(i) == 0) {
                dependencyNode.head(null);
            } else {
                dependencyNode.head(dependencyNodes.get(heads.get(i) - 1));
                dependencyNodes.get(heads.get(i) - 1).children().add(dependencyNode);
            }
        }
        if(printLogs) {
            printDependencyLabels(dependencyNodes);
        }
        return ImmutableList.copyOf(dependencyNodes);

    }

    private void printDependencyLabels(List<DependencyNode> dependencyNodes) {
        for (DependencyNode dependencyNode : dependencyNodes) {
            System.out.println(dependencyNode + " <---- " + dependencyNode.head());
        }
    }

    private Map<String, NamedEntityType> buildNamedEntityList(String input, SentenceParse sentenceParse) {
        Gson gson = new Gson();
        NERResponse nerResponse = gson.fromJson(input, NERResponse.class);
        Map<String, NamedEntityType> map = new HashMap<>();
        List<String> words = nerResponse.words();
        List<String> ners = nerResponse.tags();
        List<DependencyNode> nodes = sentenceParse.dependencyNodes();
        for (int i = 0; i < words.size(); i++) {
            String ner = ners.get(i);
            if (!(ner.equals("O") || ner.equalsIgnoreCase("MISC"))) {
                String word = words.get(i);
                NamedEntityType namedEntityType = getNamedEntitytype(ner);
                nodes.get(i).namedEntityType(namedEntityType);
                map.put(word, namedEntityType);
            }
        }
        return map;
    }

    private Map<String, NamedEntityType> buildFineGrainedNamedEntityList(String input, SentenceParse sentenceParse) {
        Gson gson = new Gson();
        NERResponse nerResponse = gson.fromJson(input, NERResponse.class);
        Map<String, NamedEntityType> map = new HashMap<>();
        List<String> words = nerResponse.words();
        List<String> ners = nerResponse.tags();
        List<DependencyNode> dependencyNodes = sentenceParse.dependencyNodes();
        for (int i = 0; i < words.size(); i++) {
            String ner = ners.get(i);
            if (!(ner.equals("O") || ner.equalsIgnoreCase("MISC"))) {
                String word = words.get(i);
                NamedEntityType namedEntityType = NamedEntityType.fromString(ner);
                dependencyNodes.get(i).namedEntityType(namedEntityType);
                map.put(word, namedEntityType);
            }

        }
        return map;
    }

    private Set<NamedEntitySpan> buildNamedEntitySpans(String ner, SentenceParse sentenceParse) {
        Gson gson = new Gson();
        NERResponse nerResponse = gson.fromJson(ner, NERResponse.class);
        Set<NamedEntitySpan> spanList = new HashSet<>();
        List<DependencyNode> dependencyNodes = sentenceParse.dependencyNodes();
        NamedEntitySpan span = null;
        List<String> nerTags = nerResponse.tags();
        if (nerTags.size() != dependencyNodes.size()) {
            System.out.println("NER and Dependency have been tokenized separately. The number of tokens do not match. ");
        }
        for (int i = 0; i < nerTags.size(); i++) {
            String tag = nerTags.get(i);
            DependencyNode token = dependencyNodes.get(i);
            if (tag.startsWith("B-") || tag.startsWith("U-")) {
                if (span != null) {
                    span.endIndex(i); // end index is exclusive
                    spanList.add(span);
                }
                span = new NamedEntitySpan();
                span.type(NamedEntityType.fromString(tag.replace("B-", "")));
                span.tokens().add(token);
                span.startIndex(i);
            } else if (tag.startsWith("I-")) {
                span.tokens().add(token);
            } else if (tag.startsWith("L-")) {
                span.tokens().add(token);
                span.endIndex(i + 1); // end index is exclusive
                spanList.add(span);
                span = null;
            } else if (tag.equalsIgnoreCase("O") && span != null) {
                span.endIndex(i); // end index is exclusive
                spanList.add(span);
                span = null;
                // when there is no tag, don't create any span
            }
            if (i == (nerTags.size() - 1) && span != null) { // add the last span
                spanList.add(span);
            }
        }
        return spanList;
    }

}
