package net.synqg.qg.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;

/**
 * @author viswa
 */
@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
public class QaPair {

    private String question;
    private String answer;
    private String templateName;
    private TemplateUnitList templateUnitList;

    public QaPair(String question, String answer, String templateName) {
        this.answer = answer;
        this.question = question;
        this.templateName = templateName;
    }

    public QaPair(TemplateUnitList templateUnitList, String answer, String templateName) {
        this.answer = answer;
        this.question = templateUnitList.formSentence();
        this.templateName = templateName;
        this.templateUnitList = templateUnitList;
    }

    @Override
    public String toString() {
        return question + ", ," + answer + " (" + templateName + ")";
    }


}
