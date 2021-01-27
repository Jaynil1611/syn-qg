package net.synqg.qg.nlg.qgtemplates.depqgtemplates;


import net.synqg.qg.nlg.factories.QuestionGeneratorHelper;
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
public class PcompQgTemplate extends AbstractDepQgTemplate {


    // “We have no information on whether users are at risk”  pcomp(on, are)
    // “They heard about you missing classes” pcomp(about, missing)
    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();

        String auxWord = verb().mainAuxWord().trim();
        String lemma = verb().lemma().trim();

        //object based question generation
        String question = "What" + " " + auxWord + " " + subject().form() + " " + verb().secondaryAuxword()
                + " " + lemma + " " + extraField() + " ?";
        String answer = dObject();
        qaPairs.add(new QaPair(question, answer,  templateName() + "::OBJECT-BASED"));


        //subject based question generation
        if (subject().namedEntityType() == null || (subject().namedEntityType() != null &&
                subject().namedEntityType().equals(NamedEntityType.MISC))) {
            subject().namedEntityType(QuestionGeneratorHelper.pronounNerMap.get(dObject().toLowerCase().trim()));
        }
        String whWordSubject = NAMED_ENTITY_TYPE_SUBJECT_MAP.getOrDefault(subject().namedEntityType(), "What");
        String questionSubject = whWordSubject + " " + verb().verb() + " " + dObject() + " ?";
        String answerSubject = subject().form();
        qaPairs.add(new QaPair(questionSubject, answerSubject, templateName()+"SUBJECT-BASED"));

        //auxillary based question generation
        String aux = verb().mainAuxWord().trim();
        if (aux.equals("")) {
            aux = "Does";
        }

        String lemmaBool = verb().boolQuesVerb().split(",")[1].trim();

        String questionAuxillary = aux + " " + subject().form() + " " + verb().secondaryAuxword().trim() + " " + lemmaBool
                + " " + dObject() + " ?";

        String answerAux = verb().boolQuesVerb().split(",")[0].trim();
        qaPairs.add(new QaPair(questionAuxillary, answerAux, templateName()+"::BOOLEAN"));


        return qaPairs;

    }

}
