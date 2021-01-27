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
public class PersonQgTemplate extends AbstractNerQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();
        String question;
        String answer = this.answer();
        if (isSubject()) {
            TemplateUnitList personTemplate = new TemplateUnitList() {
                {
                    add("Who", "Who");
                    add("Verb", verb().verb());
                    add("Subject", subject());
                    add("Object", (object() != null ? " " + object() : ""));
                    add("EXTRA-FIELD", extraField());
                    add("?", "?");
                }
            };
            //question = "Who " + verb().verb() + (object() != null ? " " + object() : "") + extraField() + " ?";
            qaPairs.add(new QaPair(personTemplate.formSentence(), answer, templateName() + "::WITH-SUBJECT", personTemplate));
        } else {
            if (object() == null) {
                return qaPairs;
            } else {
                TemplateUnitList personTemplate = new TemplateUnitList() {
                    {
                        add("Whom", "Whom");
                        add("Object", object());
                        add("Verb-Lemma", verb().lemma());
                        add("EXTRA-FIELD", extraField());
                        add("?", "?");
                    }
                };
                // question = "Whom " + object() + " " + verb().lemma() + " " + extraField() + " ?";
                question = personTemplate.formSentence();
                qaPairs.add(new QaPair(question, answer, templateName() + "::WITHOUT-SUBJECT", personTemplate));
            }
        }

        return qaPairs;
    }

}

