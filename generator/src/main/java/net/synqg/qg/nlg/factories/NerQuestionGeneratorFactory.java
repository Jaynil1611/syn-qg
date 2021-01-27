package net.synqg.qg.nlg.factories;

import net.synqg.qg.nlg.qgtemplates.QgTemplate;
import net.synqg.qg.nlg.qgtemplates.nerqgtemplates.GenericNerQgTemplate;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.NamedEntitySpan;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.nlp.labels.SemanticRoleLabel;
import net.synqg.qg.nlp.SemanticRole;
import net.synqg.qg.nlp.SemanticRoleList;
import net.synqg.qg.service.IQgAnalysis;
import net.synqg.qg.nlg.qgtemplates.nerqgtemplates.AbstractNerQgTemplate;
import net.synqg.qg.nlg.qgtemplates.nerqgtemplates.DateQgTemplate;
import net.synqg.qg.nlg.qgtemplates.nerqgtemplates.LocationQgTemplate;
import net.synqg.qg.nlg.qgtemplates.nerqgtemplates.NumberQgTemplate;
import net.synqg.qg.nlg.qgtemplates.nerqgtemplates.PersonQgTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author viswa
 */
public class NerQuestionGeneratorFactory extends AbstractQuestionGeneratorFactory {

    public static final String SUBJECT = "subject";
    public static final String OBJECT = "object";
    public static final String ANSWER = "answer";
    public static final String IS_SUBJECT = "issubject";

    private String removeText(String first, String second) {
        return second.replace(first, "");
    }

    private Boolean checkNamedEnityInSrlMap(Map<SemanticRoleLabel, String> semanticRoleLabelStringMap, String text) {
        for (Map.Entry<SemanticRoleLabel, String> semanticRoleLabelStringEntry : semanticRoleLabelStringMap.entrySet()) {
            if (semanticRoleLabelStringEntry.getValue().contains(text)) {
                return true;
            }
        }
        return false;
    }

    private Boolean checkNamedEnityInSrlMap(SemanticRoleList semanticRoleLabelStringMap, String text) {
        for (SemanticRole semanticRoleLabelStringEntry : semanticRoleLabelStringMap) {
            if (semanticRoleLabelStringEntry
                    .tokens().stream()
                    .map(DependencyNode::form)
                    .collect(Collectors.toList()).contains(text)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, NamedEntityType> getQuestionableEntities(IQgAnalysis qgAnalysis) {

        Map<String, NamedEntityType> filteredEntities = new HashMap<>();

        //List<Map<SemanticRoleLabel, String>> semanticRoleList = qgAnalysis.getSemanticRoleLabels();
        List<SemanticRoleList> semanticRoleList = qgAnalysis.getSrlSpans();
        Map<Integer, Map<NamedEntityType, List<String>>> namedEntitySemanticMap = new HashMap<>();

        int index = 0;
        //for (Map<SemanticRoleLabel, String> semanticRoleMap : qgAnalysis.getSemanticRoleLabels()) {
        for (SemanticRoleList semanticRoleMap : qgAnalysis.getSrlSpans()) {
            for (Map.Entry<String, NamedEntityType> namedEntityTypeSet : qgAnalysis.getNerLabels().entrySet()) {

                if (checkNamedEnityInSrlMap(semanticRoleMap, namedEntityTypeSet.getKey())) {
                    if (namedEntitySemanticMap.containsKey(index)) {

                        if (namedEntitySemanticMap.get(index).containsKey(namedEntityTypeSet.getValue())) {
                            namedEntitySemanticMap.get(index).get(namedEntityTypeSet.getValue())
                                    .add(namedEntityTypeSet.getKey());
                        } else {
                            List<String> tempArray = new ArrayList<>();
                            tempArray.add(namedEntityTypeSet.getKey());
                            namedEntitySemanticMap.get(index).put(namedEntityTypeSet.getValue(),
                                    tempArray);
                        }

                    } else {
                        Map<NamedEntityType, List<String>> temp = new HashMap<>();
                        List<String> tempArray = new ArrayList<>();
                        tempArray.add(namedEntityTypeSet.getKey());
                        temp.put(namedEntityTypeSet.getValue(), tempArray);
                        namedEntitySemanticMap.put(index, temp);
                    }
                }
            }
            index = index + 1;
        }

        for (Integer j : namedEntitySemanticMap.keySet()) {
            for (NamedEntityType namedEntityType : namedEntitySemanticMap.get(j).keySet()) {

                List<String> namedEntities = namedEntitySemanticMap.get(j).get(namedEntityType);
                if (namedEntities.size() > 1) {
                    AbstractQuestionGeneratorFactory.Subject subject
                            = AbstractQuestionGeneratorFactory.getSubject(semanticRoleList.get(j), qgAnalysis.getSentenceParse());
                    for (String text : namedEntities) {
                        if (subject != null && subject.form().contains(text)) {
                            filteredEntities.put(text, namedEntityType);
                        }
                    }
                } else {
                    filteredEntities.put(namedEntities.get(0), namedEntityType);
                }
            }
        }

        return filteredEntities;
    }


    //@Override
    public List<QgTemplate> extractTemplateold(IQgAnalysis qgAnalysis) {
        List<QgTemplate> qgTemplates = new ArrayList<>();
        //List<Map<SemanticRoleLabel, String>> srlList = qgAnalysis.getSemanticRoleLabels();
        List<SemanticRoleList> srlList = qgAnalysis.getSrlSpans();
        Map<String, NamedEntityType> filteredEntities = getQuestionableEntities(qgAnalysis);
        for (Map.Entry<String, NamedEntityType> namedEntityTypeSet : filteredEntities.entrySet()) {
            for (SemanticRoleList semanticRoleMap : srlList) {
                for (SemanticRole semanticRole : semanticRoleMap) {

                    if (semanticRole.type().isModifier()) {
                        // skip AM-Arguments
                        continue;
                    }

                    if (QuestionGeneratorHelper.containsA0orA1andOneMoreArgument(semanticRoleMap)
                            && semanticRole.tokens().stream().map(x -> x.form()).collect(Collectors.toList())
                            .contains(namedEntityTypeSet.getKey())) {

                        Map<String, String> subjectObjectMap = new HashMap<>();

                        Optional<String> objectOpt = getObjectPhrase(semanticRoleMap, null, false);
                        String object = null;
                        if (objectOpt.isPresent()) {
                            object = objectOpt.get();
                        }

                        Subject subjectO = AbstractQuestionGeneratorFactory.getSubject(semanticRoleMap,
                                qgAnalysis.getSentenceParse());
                        String subject = subjectO.form();
                        if (!subject.contains(namedEntityTypeSet.getKey())
                                && object != null && !object.contains(namedEntityTypeSet.getKey())) {
                            return qgTemplates;
                        }

                        subjectObjectMap.put(NerQuestionGeneratorFactory.IS_SUBJECT, "false");
                        if (semanticRole.form().equals(subject)) {
                            subjectObjectMap.put(NerQuestionGeneratorFactory.IS_SUBJECT, "true");
                            subjectObjectMap.put(NerQuestionGeneratorFactory.ANSWER, namedEntityTypeSet.getKey());
                            subject = removeText(namedEntityTypeSet.getKey(), semanticRole.form());// TODO: use tokens instead of form
                            subjectObjectMap.put(NerQuestionGeneratorFactory.SUBJECT, subject);
                            subjectObjectMap.put(NerQuestionGeneratorFactory.OBJECT, object);


                        } else if (semanticRole.form().equals(object)) {
                            subjectObjectMap.put(NerQuestionGeneratorFactory.ANSWER, namedEntityTypeSet.getKey());
                            subjectObjectMap.put(NerQuestionGeneratorFactory.OBJECT, subject);
                            object = removeText(namedEntityTypeSet.getKey(), semanticRole.form());// TODO: use tokens instead of form
                            subjectObjectMap.put(NerQuestionGeneratorFactory.SUBJECT, object);
                        } else {
                            continue;
                        }

                        AbstractNerQgTemplate qgTemplate;
                        switch (namedEntityTypeSet.getValue()) {
                            case DATE:
                                qgTemplate = new DateQgTemplate();
                                break;
                            case PERSON:
                                qgTemplate = new PersonQgTemplate();
                                break;
                            case LOCATION:
                                qgTemplate = new LocationQgTemplate();
                                break;
                            case NUMBER:
                                qgTemplate = new NumberQgTemplate();
                                break;
                            default:
                                qgTemplate = null;
                        }
                        if (qgTemplate != null) {
                            qgTemplate.trigger(namedEntityTypeSet.getKey() + "::" + namedEntityTypeSet.getValue());
                            qgTemplate.subject(subjectObjectMap.get(SUBJECT));
                            qgTemplate.object(subjectObjectMap.get(OBJECT));
                            qgTemplate.extraField(getExtraFields(semanticRoleMap, new ArrayList<>(), subjectObjectMap.get(OBJECT)));
                            qgTemplate.verb(getVerb(semanticRoleMap));
                            qgTemplate.answer(subjectObjectMap.get(ANSWER));
                            qgTemplate.isSubject(subjectObjectMap.get(IS_SUBJECT).equals("true"));
                            qgTemplates.add(qgTemplate);

                        }
                    }
                }
            }
        }

        return qgTemplates;
    }

    @Override
    public List<QgTemplate> extractTemplate(IQgAnalysis qgAnalysis) {
        List<QgTemplate> qgTemplates = new ArrayList<>();
        for (NamedEntitySpan namedEntitySpan : qgAnalysis.getNamedEntitySpans()) {
            GenericNerQgTemplate nerQgTemplate = new GenericNerQgTemplate(qgAnalysis.getSentenceParse(), namedEntitySpan);
            switch (namedEntitySpan.type()) {
                case PERCENT:
                    nerQgTemplate.setWhTerm("how much percent");
                    break;
                case LOCATION:
                    nerQgTemplate.setWhTerm("which place");
                    break;
                case ORGANIZATION:
                    nerQgTemplate.setWhTerm("which organization");
                    break;
                case GEOGRAPHICAL_ENTITY:
                    nerQgTemplate.setWhTerm("which place");
                    break;
                case CARDINAL:
                    nerQgTemplate.setWhTerm("how many");
                    break;
                case MONEY:
                    nerQgTemplate.setWhTerm("how much money");
                    break;
                default:
                    nerQgTemplate = null;
            }
            if (nerQgTemplate != null) {
                qgTemplates.add(nerQgTemplate);
            }
        }
        return qgTemplates;
    }
}
