package net.synqg.qg.nlg.qgtemplates.srlqgtemplates;

import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;
import net.synqg.qg.service.QaPair;

import java.util.Collections;
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
public class AmMnrQgTemplate extends AbstractSrlQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        TemplateUnitList srlTemplate = new TemplateUnitList() {
            {
                add("How", "How");
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject().form());
                add("SecondAux", verb().secondaryAuxword().trim());
                add("Verb", verb().lemma());
                add("Object",  (object() != null ? " " + object() : ""));
                add("ExtraField", extraField());
                add("?", "?");
            }
        };
        //String question = "How" + " " + verb().mainAuxWord() + " " + subject().text() + " " + verb().secondaryAuxword().trim() + " " + verb().lemma() + (object() != null ? " " + object() : "") + " " + extraField() + " ?";

        String answer = semanticText();

        return Collections.singletonList(new QaPair(srlTemplate.formSentence(), answer, templateName(), srlTemplate));
    }

}
