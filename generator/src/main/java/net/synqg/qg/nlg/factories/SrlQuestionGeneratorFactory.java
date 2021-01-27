package net.synqg.qg.nlg.factories;

import net.synqg.qg.nlg.qgtemplates.QgTemplate;
import net.synqg.qg.nlp.labels.SemanticRoleLabel;
import net.synqg.qg.nlp.SemanticRole;
import net.synqg.qg.nlp.SemanticRoleList;
import net.synqg.qg.service.IQgAnalysis;
import net.synqg.qg.nlg.qgtemplates.srlqgtemplates.AbstractSrlQgTemplate;
import net.synqg.qg.nlg.qgtemplates.srlqgtemplates.AmCauQgTemplate;
import net.synqg.qg.nlg.qgtemplates.srlqgtemplates.AmDirQgTemplate;
import net.synqg.qg.nlg.qgtemplates.srlqgtemplates.AmExtQgTemplate;
import net.synqg.qg.nlg.qgtemplates.srlqgtemplates.AmLocQgTemplate;
import net.synqg.qg.nlg.qgtemplates.srlqgtemplates.AmMnrQgTemplate;
import net.synqg.qg.nlg.qgtemplates.srlqgtemplates.AmPncQgTemplate;
import net.synqg.qg.nlg.qgtemplates.srlqgtemplates.AmTmpQgTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SRL Question Generation Factory. (Triggers specific SRL templates.)
 *
 * @author kaustubhdholé.
 */
public class SrlQuestionGeneratorFactory extends AbstractQuestionGeneratorFactory {

    @Override
    public List<QgTemplate> extractTemplate(IQgAnalysis qgAnalysis) {

        List<QgTemplate> qgTemplates = new ArrayList<>();

        List<SemanticRoleList> srlList =
                qgAnalysis.getSrlSpans()
                        .stream()
                        .filter(srl -> QuestionGeneratorHelper.containsA0orA1andOneMoreArgument(srl))
                        .filter(srl -> implicationService.computeEntailedPolarityOrDefault(srl.verb(), true))
                        .collect(Collectors.toList());

        for (SemanticRoleList spanList : srlList) {
            for (SemanticRole span : spanList.spans()) {
                SemanticRoleLabel semanticRoleLabel = span.type();
                Optional<String> object = extractObject(spanList);
                AbstractSrlQgTemplate qgTemplate;
                switch (semanticRoleLabel) {
                    case ARGM_LOC:
                        qgTemplate = new AmLocQgTemplate();
                        break;
                    case ARGM_MNR:
                        qgTemplate = new AmMnrQgTemplate();
                        break;
                    case ARGM_PNC:
                    case ARGM_PRP:
                        qgTemplate = new AmPncQgTemplate();
                        break;
                    case ARGM_CAU:
                        qgTemplate = new AmCauQgTemplate();
                        break;
                    case ARGM_TMP:
                        qgTemplate = new AmTmpQgTemplate();
                        break;
                    case ARGM_DIR:
                        qgTemplate = new AmDirQgTemplate();
                        break;
                    case ARGM_EXT:
                        qgTemplate = new AmExtQgTemplate();
                        break;
                    default:
                        qgTemplate = null;
                }

                if (qgTemplate != null) {
                    // extract subject from A0
                    Subject subject = getSubject(spanList, qgAnalysis.getSentenceParse());
                    if (subject == null) {
                        continue;
                    }
                    qgTemplate.subject(subject);

                    String objectStr = null;
                    if (object.isPresent()) {
                        objectStr = object.get();
                    }
                    qgTemplate.trigger(semanticRoleLabel + "::" + spanList.get(semanticRoleLabel).get().form());
                    qgTemplate.object(objectStr);
                    qgTemplate.extraField(getExtraFields(spanList, Collections.singletonList(semanticRoleLabel), objectStr));
                    qgTemplate.verb(getVerb(spanList));
                    qgTemplate.semanticText(spanList.get(semanticRoleLabel).get().form());
                    qgTemplates.add(qgTemplate);
                }
            }
        }
        return qgTemplates;
    }
}
