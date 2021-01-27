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
public class CcompQgTemplate extends AbstractDepQgTemplate {


    //  “He says that you like to swim” ccomp (says, like)
    //  “I am certain that he did it” ccomp(certain, did)
    //  “I admire the fact that you are honest” ccomp(fact, honest)
    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();

        //(1) object based question generation
        TemplateUnitList objectQuestionTemplate = new TemplateUnitList() {
            {
                add("What", "What");
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject().form());
                add("SecondAux", verb().secondaryAuxword());
                add("Verb", verb().lemma());
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };
        //String question = "What" + " " + verb().mainAuxWord().trim() + " " + subject().text() + " " + verb().secondaryAuxword() + " " + verb().lemma() + " " + extraField() + " ?";
        String question = objectQuestionTemplate.formSentence();
        String answer = dObject();
        qaPairs.add(new QaPair(question, answer, templateName() + "::OBJECT-BASED", objectQuestionTemplate));

        //(2) Subject based question generation
        if (subject().namedEntityType() == null || (subject().namedEntityType() != null &&
                subject().namedEntityType().equals(NamedEntityType.MISC))) {
            subject().namedEntityType(QuestionGeneratorHelper.pronounNerMap.get(dObject().toLowerCase().trim()));
        }
        String whWordSubject = NAMED_ENTITY_TYPE_SUBJECT_MAP.getOrDefault(subject().namedEntityType(), "what");

        TemplateUnitList questionSubjectTemplate = new TemplateUnitList() {
            {
                add("whWordSubject", whWordSubject);
                add("Verb", verb().verb());
                add("Object", dObject());
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };
        //String questionSubject = whWordSubject + " " + verb().verb() + " " + object() + " " + extraField() + " ?";
        String questionSubject = questionSubjectTemplate.formSentence();
        String answerSubject = subject().form();
        qaPairs.add(new QaPair(questionSubject, answerSubject, templateName() + "::SUBJECT-BASED", questionSubjectTemplate));

        // (3) Boolean based question generation
        String aux = verb().mainAuxWord().trim();
        if (aux.equals("")) {
            aux = "Does";
        }
        String lemmaBool = verb().boolQuesVerb().split(",")[1].trim();

        String finalAux = aux;
        TemplateUnitList booleanQuestionTeplate = new TemplateUnitList() {
            {
                add("MainAux", finalAux);
                add("Subject", subject().form());
                add("SecondAux", verb().secondaryAuxword());
                add("Verb", lemmaBool);
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };

        //String questionAuxillary = aux + " " + subject().text() + " " + verb().secondaryAuxword() + " " + lemmaBool + " " + object() + " " + extraField() + " ?";
        String questionAuxillary = booleanQuestionTeplate.formSentence();
        String answerAux = verb().boolQuesVerb().split(",")[0].trim();
        qaPairs.add(new QaPair(questionAuxillary, answerAux, templateName() + "::BOOLEAN", booleanQuestionTeplate));

        return qaPairs;
    }

}
