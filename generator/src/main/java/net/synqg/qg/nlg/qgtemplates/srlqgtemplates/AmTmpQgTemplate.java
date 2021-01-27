package net.synqg.qg.nlg.qgtemplates.srlqgtemplates;

import com.google.common.collect.Sets;
import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;
import net.synqg.qg.service.QaPair;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author viswa
 */
@Getter
@Setter
@Accessors(fluent = true)
public class AmTmpQgTemplate extends AbstractSrlQgTemplate {

    @Override
    public List<QaPair> generateQuestion() {
        String whWord = "When";
        //TODO: "for duration--> "For how long ?"
        if (tillWhenSet().stream().anyMatch(w -> semanticText().toLowerCase().contains(w))) {
            whWord = "Till when";
        }

        String finalWhWord = whWord;
        TemplateUnitList srlTemplate = new TemplateUnitList() {
            {
                add("WhWord", finalWhWord);
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject().form());
                add("SecondAux", verb().secondaryAuxword().trim());
                add("Verb", verb().lemma());
                add("Object",  (object() != null ? " " + object() : ""));
                add("Extra-Field", extraField());
                add("?", "?");
            }
        };

        //String question = whWord + " " + verb().mainAuxWord() + " " + subject().text() + " " + verb().secondaryAuxword().trim() + " " + verb().lemma() + (object() != null ? " " + object() : "") + " " + extraField() + " ?";

        String answer = semanticText();

        return Collections.singletonList(new QaPair(srlTemplate, answer, templateName()));
    }


    private static Set<String> tillWhenSet() {
        return Sets.newHashSet("until", "untill", "till");
    }


}

