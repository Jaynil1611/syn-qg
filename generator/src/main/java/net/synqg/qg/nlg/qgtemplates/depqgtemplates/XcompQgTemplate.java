package net.synqg.qg.nlg.qgtemplates.depqgtemplates;


import net.synqg.qg.nlg.factories.QuestionGeneratorHelper;
import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.service.QaPair;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author viswa
 */
@Getter
@Setter
@Accessors(fluent = true)
public class XcompQgTemplate extends AbstractDepQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();

        String auxWord = verb().mainAuxWord().trim();
        String lemma = verb().lemma().trim();

        // object based question generation
        TemplateUnitList objectQuestionTemplate = new TemplateUnitList() {
            {
                add("What", "What");
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject().form());
                add("SecondaryAux", verb().secondaryAuxword());
                add("Verb", verb().lemma());
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };
        //String question = "What" + " " + auxWord + " " + subject().text() + " " + verb().secondaryAuxword() + " " + lemma + " " + extraField() + " ?";
        String question = objectQuestionTemplate.formSentence();
        String answer = dObject();
        qaPairs.add(new QaPair(question, answer, templateName() + "::OBJECT-BASED", objectQuestionTemplate));

        // Subject based question generation

        if (subject().namedEntityType() == null || (subject().namedEntityType() != null &&
                subject().namedEntityType().equals(NamedEntityType.MISC))) {
            subject().namedEntityType(QuestionGeneratorHelper.pronounNerMap.get(dObject().toLowerCase().trim()));
        }
        String whWordSubject = NAMED_ENTITY_TYPE_SUBJECT_MAP.getOrDefault(subject().namedEntityType(), "What");
        TemplateUnitList subjectQuestionTemplate = new TemplateUnitList() {
            {
                add("What", whWordSubject);
                add("Verb", verb().verb());
                add("Object", dObject());
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };
        //String questionSubject = whWordSubject + " " + verb().verb() + " " + object() + " " + extraField() + " ?";
        String questionSubject = subjectQuestionTemplate.formSentence();
        String answerSubject = subject().form();
        qaPairs.add(new QaPair(questionSubject, answerSubject, templateName() + "::SUBJECT-BASED", subjectQuestionTemplate));

        //Boolean question generation by appending auxillary at the beginning.
        String aux = verb().mainAuxWord().trim();
        if (aux.equals("")) {
            aux = "Does";
        }

        String lemmaBool = verb().boolQuesVerb().split(",")[1].trim();

        String finalAux = aux;
        TemplateUnitList booleanQuestionTemplate = new TemplateUnitList() {
            {
                add("AUX", finalAux);
                add("Subject", subject().form());
                add("SecondaryAux", verb().secondaryAuxword());
                add("Verb", lemmaBool);
                add("Object", dObject());
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };
        //String questionAuxillary = aux + " " + subject().text() + " " + verb().secondaryAuxword() + " " + lemmaBool + " " + object() + " " + extraField() + " ?";
        String questionAuxillary = booleanQuestionTemplate.formSentence();
        String answerAux = verb().boolQuesVerb().split(",")[0].trim();
        qaPairs.add(new QaPair(questionAuxillary, answerAux, templateName() + "::BOOLEAN", booleanQuestionTemplate));

        return qaPairs;
    }

}
