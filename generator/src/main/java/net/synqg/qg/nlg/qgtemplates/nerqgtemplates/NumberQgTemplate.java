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
public class NumberQgTemplate extends AbstractNerQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();

        String question;
        String answer = this.answer();

        if (object() == null) {
            return qaPairs;
        }

        if (!isSubject()) {
            TemplateUnitList numberTemplate = new TemplateUnitList() {
                {
                    add("How many", "How many");
                    add("MainAux", verb().mainAuxWord().trim());
                    add("Subject", subject());
                    add("SecondAux", verb().secondaryAuxword().trim());
                    add("Object", object());
                    add("verb", verb().lemma());
                    add("?", "?");
                }
            };
            //question = "How many" + " " + verb().mainAuxWord() + " " + subject() + " " + verb().secondaryAuxword().trim() + " " + object() + " " + verb().lemma() + " ?";
            qaPairs.add(new QaPair(numberTemplate.formSentence(), answer, templateName() + "::WITHOUT-SUBJECT", numberTemplate));
        } else {
            TemplateUnitList numberTemplate = new TemplateUnitList() {
                {
                    add("How many", "How many");
                    add("MainAux", verb().mainAuxWord().trim());
                    add("Subject", subject());
                    add("SecondAux", verb().secondaryAuxword().trim());
                    add("verb", verb().lemma());
                    add("Object", object());
                    add("?", "?");
                }
            };
            //qu
            //question = "How many" + " " + verb().mainAuxWord() + " " + subject() + " " + verb().secondaryAuxword() + " " + verb().lemma() + " " + object() + " ?";
            qaPairs.add(new QaPair(numberTemplate.formSentence(), answer, templateName() + "::WITH-SUBJECT", numberTemplate));
        }

        return qaPairs;
    }

}

