package net.synqg.qg.nlg.qgtemplates.depqgtemplates;

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
public class AcompQgTemplate extends AbstractDepQgTemplate {

    // She looks very beautiful.
    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();
        TemplateUnitList objectQuestionTemplate = new TemplateUnitList() {
            {
                add("Indicate characteristics of", "Indicate characteristics of");
                add("Subject", subject().form());
                add("?", "?");
            }
        };
        //String question = "Indicate characteristics of" + " " + subject().text() + " ?";
        String question = objectQuestionTemplate.formSentence();
        String answer = dObject();
        if (answer == null) {
            return qaPairs;
        }
        qaPairs.add(new QaPair(question, answer, templateName(), objectQuestionTemplate));
        return qaPairs;
    }

}

