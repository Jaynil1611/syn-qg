package net.synqg.qg.nlg.qgtemplates.srlqgtemplates;

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
public class AmCauQgTemplate extends AbstractSrlQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();
        String question;

        //TODO: need to relook at this template:
        // Fact: Since the average faucet releases 2 gallons of water per minute, you can save up to four gallons of water every morning by turning off the tap while you brush your teeth.
        // Generated: Why can you save by turning off the tap while you brush your teeth every morning up to four gallons of water ?

        TemplateUnitList srlTemplate = new TemplateUnitList() {
            {
                add("Why", "Why");
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject().form());
                add("SecondAux", verb().secondaryAuxword().trim());
                add("Verb", verb().lemma());
                add("Object",  (object() != null ? " " + object() : ""));
                add("Extra-Field", extraField());
                add("?", "?");
            }
        };
        //question = "Why" + " " + verb().mainAuxWord() + " " + subject().text() + " " + verb().secondaryAuxword().trim() + " " + verb().lemma() + (object() != null ? " " + object() : "") + " " + extraField() + " ?";
        String answer = semanticText();
        qaPairs.add(new QaPair(srlTemplate.formSentence(), answer, templateName(), srlTemplate));
        return qaPairs;
    }

}

