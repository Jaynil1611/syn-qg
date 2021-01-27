package net.synqg.qg.nlg.qgtemplates.srlqgtemplates;

import net.synqg.qg.nlg.factories.QuestionGeneratorHelper;
import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;
import net.synqg.qg.service.QaPair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author viswa
 */
@Getter
@Setter
@Accessors(fluent = true)
public class AmLocQgTemplate extends AbstractSrlQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();
        String question;

        String questype = "Where";
        String pattern = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(semanticText());

        if (m.find()) {
            questype = "At what number";
        }

        String finalQuestype = questype;
        TemplateUnitList srlTemplate = new TemplateUnitList() {
            {
                add("WhType", finalQuestype);
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject().form());
                add("SecondAux", verb().secondaryAuxword().trim());
                add("Verb", verb().lemma());
                add("Object",  (object() != null ? " " + object() : ""));
                add("Extra-Field", extraField());
                add("?", "?");
            }
        };
           // question = questype + " " + verb().mainAuxWord() + " " + subject().text() + " " + verb().secondaryAuxword().trim() + " " + verb().lemma() + " " +  (object() != null ? " " + object() : "") + " " + extraField() + " ?";
        String answer = semanticText();
        qaPairs.add(new QaPair(srlTemplate, answer, templateName()+"MAIN"));

        // subject based question generation
        if (subject().namedEntityType() == null) {
            subject().namedEntityType(QuestionGeneratorHelper.pronounNerMap.get(subject().form().toLowerCase().trim()));
        }
        String whWordSubject = NAMED_ENTITY_TYPE_SUBJECT_MAP.getOrDefault(subject().namedEntityType(), "What");
        srlTemplate = new TemplateUnitList() {
            {
                add("WhType", whWordSubject);
                add("Verb", verb().verb());
                add("Object",  (object() != null ? " " + object() : ""));
                add("Semantic-Text", semanticText());
                add("?", "?");
            }
        };
        //String questionSubject = whWordSubject + " " + verb().verb() + (object() != null ? " " + object() : "") + " " + semanticText() + " ?";
        String answerSubject = subject().form();
        qaPairs.add(new QaPair(srlTemplate, answerSubject, templateName()+"::SUBJECT-BASED"));//TODO: object needs to be there.

        //auxillary based question generation
        String auxWord = verb().mainAuxWord().trim();
        if (auxWord.equals("")) {
            auxWord = "Does";
        }

        String lemma = verb().boolQuesVerb().split(",")[1];
        String questionAuxillary;

        String finalAuxWord = auxWord;
        srlTemplate = new TemplateUnitList() {
            {
                add("MainAux", finalAuxWord);
                add("Subject", subject().form());
                add("SecondAux", verb().secondaryAuxword().trim());
                add("Verb", lemma);
                add("Object",  (object() != null ? " " + object() : ""));
                add("Semantic-Text", semanticText());
                add("Extra-Field", extraField());
                add("?", "?");
            }
        };
        /*if (object() != null) {
            questionAuxillary = auxWord + " " + subject().text() + " " + verb().secondaryAuxword().trim() + " " + lemma + " " + " " + object()
                    + semanticText() + " " + extraField() + " ?";
        } else {
            questionAuxillary = auxWord + " " + subject().text() + " " + verb().secondaryAuxword().trim() + " " + lemma +
                    " " + extraField() + " " + semanticText() + " ?";
        }*/

        String answerAux = verb().boolQuesVerb().split(",")[0];
        qaPairs.add(new QaPair(srlTemplate, answerAux, templateName()+"::BOOLEAN"));

        return qaPairs;
    }

}

