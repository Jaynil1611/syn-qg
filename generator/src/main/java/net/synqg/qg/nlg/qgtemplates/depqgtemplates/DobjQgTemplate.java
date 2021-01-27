package net.synqg.qg.nlg.qgtemplates.depqgtemplates;

import com.google.common.collect.Sets;
import net.synqg.qg.nlg.factories.QuestionGeneratorHelper;
import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.service.QaPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author viswa
 */
@Getter
@Setter
@Accessors(fluent = true)
public class DobjQgTemplate extends AbstractDepQgTemplate {

    private boolean polarity;

    public DobjQgTemplate(boolean polarity) {
        this.polarity = polarity;
    }

    //  “She gave me a raise” dobj(gave, raise)
    //  “They win the lottery” dobj(win, lottery)
    // "She gave me" dobj(gave, me) -->  We need to replace "what" with who if answer is
    // a person (NE_person, me, her, him, them, etc)
    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();

        NamedEntityType namedEntityType = namedEntityType();
        if (namedEntityType == null || namedEntityType.equals(NamedEntityType.MISC)) {
            namedEntityType = QuestionGeneratorHelper.pronounNerMap.get(dObject().toLowerCase().trim());
        }

        String mainAuxWord = verb().mainAuxWord().trim();
        String secondaryAuxWord = verb().secondaryAuxword().trim();

        String lemma = verb().lemma().trim();

        // Object based Question Generation
        String whWord = NAMED_ENTITY_TYPE_OBJECT_MAP.getOrDefault(namedEntityType, "What");
        TemplateUnitList questionTemplate = new TemplateUnitList() {
            {
                add("WH-WORD", whWord);
                add("mainAuxWord", mainAuxWord);
                add("Subject", subject().form().trim());
                add("SecondaryAux", secondaryAuxWord);
                add("Verb", lemma);
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };
        //String question = whWord + " " + mainAuxWord + " " + subject().text().trim() + " " + secondaryAuxWord + " " + lemma + " " + extraField().trim() + " ?";
        String question = questionTemplate.formSentence();
        String answer = dObject();
        qaPairs.add(new QaPair(question, answer, templateName() + "::OBJECT-BASED", questionTemplate));

        //Subject based Question generation
        if (subject().namedEntityType() == null || (subject().namedEntityType() != null &&
                subject().namedEntityType().equals(NamedEntityType.MISC))) {
            subject().namedEntityType(QuestionGeneratorHelper.pronounNerMap.get(dObject().toLowerCase().trim()));
        }

        String whWordSubject;
        if (subject().namedEntityType() == null && heOrSheOrWe().contains(subject().form().toLowerCase().trim())) {
            whWordSubject = "Who";
        } else {
            whWordSubject = NAMED_ENTITY_TYPE_SUBJECT_MAP.getOrDefault(subject().namedEntityType(), "What");

        }

        TemplateUnitList subjQuestionTemplate = new TemplateUnitList() {
            {
                add("WH-WORD", whWordSubject);
                add("Verb", verb().verb());
                add("Object", dObject());
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };
        //String questionSubject = whWordSubject + " " + verb().verb() + " " + object() + " " + extraField() + " ?";
        String questionSubject = subjQuestionTemplate.formSentence();
        String answerSubject = subject().form();
        qaPairs.add(new QaPair(questionSubject, answerSubject, templateName() + "::SUBJECT-BASED", subjQuestionTemplate));

        //auxillary based question generation
        String aux = verb().mainAuxWord().trim();
        if (aux.equals("")) {
            aux = "Does";
        }

        String lemmaBool = verb().boolQuesVerb().split(",")[1].trim();

        String finalAux = aux;
        TemplateUnitList booleanQuestionTemplate = new TemplateUnitList() {
            {
                add("MainAuxWord", finalAux);
                add("Subject", subject().form());
                add("SecondaryAux", secondaryAuxWord);
                add("Verb", lemmaBool);
                add("Object", dObject());
                add("ExtraField", extraField());
                add("?", "?");
            }
        };
        // String questionAuxillary = aux + " " + subject().text() + " " + secondaryAuxWord + " " + lemmaBool + " " + object() + " " + extraField() + " ?";
        String questionAuxillary = booleanQuestionTemplate.formSentence();
        String answerAux = verb().boolQuesVerb().split(",")[0].trim();
        qaPairs.add(new QaPair(questionAuxillary, answerAux, templateName() + "::BOOLEAN", booleanQuestionTemplate));

        if (hypernym() != null && !hypernym().equalsIgnoreCase("")) {

            String[] hypernyms = hypernym().split("_");
            String hypernymCombined = "";
            for (String hypernymSub : hypernyms) {
                hypernymCombined = hypernymCombined + " " + hypernymSub;
            }
            String finalHypernymCombined = hypernymCombined;
            TemplateUnitList wordNetQuestionTemplate = new TemplateUnitList() {
                {
                    add("Which", "Which");
                    add("Hypernym", finalHypernymCombined);
                    add("mainAuxWord", mainAuxWord);
                    add("Subject", subject().form());
                    add("SecondaryAux", secondaryAuxWord);
                    add("Verb", lemma);
                    add("ExtraField", extraField().trim());
                    add("?", "?");
                }
            };
            //String questionWordnet = "Which" + hypernymCombined + " " + mainAuxWord + " " + subject().text() + " " + secondaryAuxWord + " " + lemma + " " + extraField() + "?";
            String questionWordnet = wordNetQuestionTemplate.formSentence();
            String answerWordnet = dObject();
            qaPairs.add(new QaPair(questionWordnet, answerWordnet, templateName() + "::WORDNET", wordNetQuestionTemplate));

        }

        // TODO: shift this from here since Pobj also has a similar logic.
        // if the polarity is negative, then only keep the boolean question with the opposite answer and delete the rest.
        if (!polarity) {
            qaPairs = qaPairs.stream()
                    .filter(q -> q.templateName().contains("BOOLEAN"))
                    .map(q -> {
                        q.answer("No");
                        return q;
                    }).collect(Collectors.toList());
        }

        return qaPairs;
    }

    private static Set<String> heOrSheOrWe() {
        return Sets.newHashSet("he", "she", "they", "we", "I");
    }

}
