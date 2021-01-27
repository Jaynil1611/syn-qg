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
public class LocationQgTemplate extends AbstractNerQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();

        String question;
        String answer;

        if (!isSubject()) {
            TemplateUnitList locationTemplate = new TemplateUnitList() {
                {
                    add("Where", "Where");
                    add("MainAux", verb().mainAuxWord().trim());
                    add("Object", object());
                    add("SecondAux", verb().secondaryAuxword().trim());
                    add("verb", verb().lemma());
                    add("subj", subject());
                    add("?", "?");
                }
            };
            //question = "Where" + " " + verb().mainAuxWord() + " " + object() + " " + verb().secondaryAuxword() + " " + verb().lemma() + " " + subject() + " ?";
            answer = this.answer();
            qaPairs.add(new QaPair(locationTemplate.formSentence(), answer, templateName() + "::WITHOUT-SUBJECT", locationTemplate));
        } else {
            if (object() == null) {
                return qaPairs;
            }
            TemplateUnitList locationTemplate = new TemplateUnitList() {
                {
                    add("What", "What");
                    add("Verb", verb().verb());
                    add("Object", object());
                    add("subj", subject());
                    add("?", "?");
                }
            };
            question = "What" + verb().verb() + " " + object() + " " + subject() + " ?";
            answer = this.answer();
            qaPairs.add(new QaPair(question, answer, templateName() + "::WITH-SUBJECT", locationTemplate));

        }
        return qaPairs;
    }

}

