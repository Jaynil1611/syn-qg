package net.synqg.qg.nlg.qgtemplates;

import lombok.experimental.Accessors;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.service.QaPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author viswa
 */
@Accessors(fluent = true)
public interface QgTemplate {

    Map<NamedEntityType, String> NAMED_ENTITY_TYPE_OBJECT_MAP = new HashMap<NamedEntityType, String>() {

        {
            put(NamedEntityType.ORGANIZATION, "Where");
            put(NamedEntityType.PERSON, "Whom");
            put(NamedEntityType.LOCATION, "Where");
            put(NamedEntityType.DATE, "When");
        }
    };

     Map<NamedEntityType, String> NAMED_ENTITY_TYPE_SUBJECT_MAP = new HashMap<NamedEntityType, String>() {

        {
            put(NamedEntityType.ORGANIZATION, "Who");
            put(NamedEntityType.PERSON, "Who");
            put(NamedEntityType.LOCATION, "What");
            put(NamedEntityType.DATE, "When");
        }
    };


    /**
     * Generate a Question for user sentence.
     *
     * @return list of question answer pairs
     */
     List<QaPair> generateQuestion();

     default String templateName() {
        return this.getClass().getSimpleName();
     }

     String trigger();

}
