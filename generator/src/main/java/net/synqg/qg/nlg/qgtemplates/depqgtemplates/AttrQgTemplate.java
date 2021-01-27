package net.synqg.qg.nlg.qgtemplates.depqgtemplates;

import net.synqg.qg.nlg.factories.QuestionGeneratorHelper;
import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;
import net.synqg.qg.nlp.labels.NamedEntityType;
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
public class AttrQgTemplate extends AbstractDepQgTemplate {


    // Bill is an honest man.
    // Donald is rich.
    // The building is huge.
    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();
        TemplateUnitList attributeQuestion = new TemplateUnitList() {
            {
                add("How would you describe", "How would you describe");
                add("Subject", QuestionGeneratorHelper.pronounMap.getOrDefault(subject().form().toLowerCase().trim(), subject().form()));
                add("?", "?");
            }
        };
        //String question = "How would you describe" + " " + QuestionGeneratorHelper.pronounMap.getOrDefault(subject().text().toLowerCase().trim(), subject().text()) + " ?";
        String question = attributeQuestion.formSentence();
        String answer = subject().form() + " " + verb().verb() + " " + dObject();
        qaPairs.add(new QaPair(question, answer, templateName() + "::DESCRIBE", attributeQuestion));

        NamedEntityType namedEntityType = namedEntityType();
        if (namedEntityType == null || namedEntityType == NamedEntityType.MISC) {
            namedEntityType = QuestionGeneratorHelper.pronounNerMap.get(subject().form().toLowerCase().trim());
        }

        if (subject().namedEntityType() == null || (subject().namedEntityType() != null &&
                subject().namedEntityType().equals(NamedEntityType.MISC))) {
            subject().namedEntityType(QuestionGeneratorHelper.pronounNerMap.get(dObject().toLowerCase().trim()));
        }
        String whtype = NAMED_ENTITY_TYPE_SUBJECT_MAP.getOrDefault(namedEntityType, "What");
        TemplateUnitList template1 = new TemplateUnitList() {
            {
                add("WHTYPE", whtype);
                add("Verb", verb().verb());
                add("Object", dObject());
                add("ExtraField", extraField());
                add("?", "?");
            }
        };

        //String question1 = whtype + " " + verb().verb() + " " + object() + " " + extraField() + "?";
        String question1 = template1.formSentence();
        String answer1 = subject().form();
        qaPairs.add(new QaPair(question1, answer1, templateName() + "::OTHER1"));

        //auxillary based question generation
        TemplateUnitList booleanTemplate = new TemplateUnitList() {
            {
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject().form());
                add("SecondAux", verb().secondaryAuxword().trim());
                add("Object", dObject());
                add("ExtraField", extraField());
                add("?", "?");
            }
        };
        //String questionAuxillary = verb().mainAuxWord().trim() + " " + subject().text() + " " + verb().secondaryAuxword().trim() + " " + object() + " " + extraField() + " ?";
        String questionAuxillary = booleanTemplate.formSentence();
        String answerAux = verb().boolQuesVerb().split(",")[0];
        qaPairs.add(new QaPair(questionAuxillary, answerAux, templateName() + "::BOOLEAN", booleanTemplate));

        return qaPairs;
    }

}
