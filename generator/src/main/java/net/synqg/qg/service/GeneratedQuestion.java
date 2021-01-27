package net.synqg.qg.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * The generated question along-with utilities for pretty-printing them for debugging.
 *
 * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedQuestion {

    private String question;
    private String preCorrection; // Before applying back-translation. (Used for test-cases).
    private double confidence;
    private String shortAnswer;
    private String templateName;
    private String filledTemplate;
    private String trigger;

    /**
     * This is for backtracking and for SQUAD
     **/
    private String paragraph;
    /**
     * This is for SQUAD
     **/
    private int answerStartIndex;

    /*public GeneratedQuestion(QaPair qaPair, String trigger) {
        this(qaPair.question(), qaPair.question(),
                5.0, qaPair.answer(), qaPair.templateName(), qaPair.templateUnitList().prettyPrint(), trigger);
    }*/

    @Override
    public String toString() {
        return this.question;
    }

    public String prettyPrint() {
        return "*****************************************************************" + "\n"
                + "Question       : " + preCorrection + "\n"
                //+ "BackTranslated : " + question + "\n"
                + "Answer : " + shortAnswer + "\n"
                + (templateName != null ? ("Template :" + templateName + "\n") : "")
                + (trigger != null ? ("Trigger : " + trigger + "\n") : "")
                + (filledTemplate != null ? (filledTemplate + "\n") : "");
    }

    public String prettyPrintWithParagraph(String paragraph) {
        return "*****************************************************************" + "\n"
                + paragraph + "\n"
                + prettyPrint();
    }
}