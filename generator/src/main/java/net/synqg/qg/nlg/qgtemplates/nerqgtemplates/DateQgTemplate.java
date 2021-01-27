package net.synqg.qg.nlg.qgtemplates.nerqgtemplates;


import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;
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
public class DateQgTemplate extends AbstractNerQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();

        String answer = this.answer();

        if (object() == null) {
            return qaPairs;
        }
        TemplateUnitList dateTemplate = new TemplateUnitList() {
            {
                add("When", "When");
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject());
                add("SecondAux", verb().secondaryAuxword().trim());
                if (!isSubject()) {
                    add("Object", object());
                    add("verb", verb().lemma());
                } else {
                    add("verb", verb().lemma());
                    add("Object", object());
                }
                add("?", "?");
            }
        };

        //TODO: add test-cases for this.
       /* if (!isSubject()) {
            question = "When " + verb().mainAuxWord().trim() + " " + subject() + " " +
                    verb().secondaryAuxword().trim() + " " + object() + " " + verb().lemma() + " ?";
            qaPairs.add(new QaPair(question, answer, templateName() + "::WITHOUT-SUBJECT"));

        } else {
            question = "When" + " " + verb().mainAuxWord().trim() + " " + subject() + " "
                    + verb().secondaryAuxword().trim() + " " + verb().lemma() + " " + object() + " ?";
            qaPairs.add(new QaPair(question, answer, templateName() + "::WITH-SUBJECT"));

        }*/
        qaPairs.add(new QaPair(dateTemplate.formSentence(), answer, templateName(), dateTemplate));
        return qaPairs;
    }

}

