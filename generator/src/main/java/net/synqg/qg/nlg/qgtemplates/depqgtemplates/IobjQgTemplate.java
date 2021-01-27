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
public class IobjQgTemplate extends AbstractDepQgTemplate {


    //“She gave me a raise” iobj(gave, me). We need to replace "what" with who if answer
    // is a person (NE_person, me, her, him, them, etc)
    @Override
    public List<QaPair> generateQuestion() {
        List<QaPair> qaPairs = new ArrayList<>();

        NamedEntityType namedEntityType = namedEntityType();
        if (namedEntityType == null) {
            namedEntityType = QuestionGeneratorHelper.pronounNerMap.get(dObject().toLowerCase().trim());
        }
        // Object based Question Generation
        String whWord = NAMED_ENTITY_TYPE_OBJECT_MAP.getOrDefault(namedEntityType, "What");

        TemplateUnitList objectQuestionTemplate = new TemplateUnitList() {
            {
                add("What", whWord);
                add("MainAux", verb().mainAuxWord().trim());
                add("Subject", subject().form());
                add("SecondaryAux", verb().secondaryAuxword());
                add("Verb", verb().lemma());
                add("ExtraField", extraField().trim());
                add("?", "?");
            }
        };

        //String question = whWord + " " + verb().mainAuxWord().trim() + " " + subject().text() + " " + verb().secondaryAuxword().trim() + " " + verb().lemma() + " " + extraField() + " ?";
        String question = objectQuestionTemplate.formSentence();
        String answer = dObject();
        qaPairs.add(new QaPair(question, answer, templateName() + "::OBJECT", objectQuestionTemplate));
        return qaPairs;
    }

}
