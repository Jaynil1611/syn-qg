package net.synqg.qg.nlp.labels;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 13 Named Entity Types
 * http://www-nlpir.nist.gov/related_projects/muc/proceedings/ne_task.html
 */
@Slf4j
@AllArgsConstructor
public enum NamedEntityType {

    DATE("DATE"),

    LOCATION("LOCATION"),

    ORGANIZATION("ORGANIZATION"),

    PERCENT("PERCENT"),

    PERSON("PERSON"),

    MISC("MISC"),

    MONEY("MONEY"),

    TIME("TIME"),

    NONE("O"),

    GEOGRAPHICAL_ENTITY("ENTITY"), //country/city/state

    ORDINAL("ORDINAL"),

    CARDINAL("CARDINAL"),

    NUMBER("NUMBER"),

    DURATION("DURATION");

    public String val;

    public static NamedEntityType fromString(String s) {
        if (s.endsWith("PER") || s.endsWith("PERSON")) {
            return NamedEntityType.PERSON;
        }
        if (s.endsWith("ORDINAL")) {
            return NamedEntityType.ORDINAL;
        }
        if (s.endsWith("CARDINAL")) {
            return NamedEntityType.CARDINAL;
        }
        if (s.endsWith("MONEY")) {
            return NamedEntityType.MONEY;
        }
        if (s.endsWith("TIME")) {
            return NamedEntityType.TIME;
        }
        if (s.endsWith("DATE")) {
            return NamedEntityType.DATE;
        }
        if (s.endsWith("ORG")) {
            return NamedEntityType.ORGANIZATION;
        }
        if (s.endsWith("PERCENT")) {
            return NamedEntityType.PERCENT;
        }
        if (s.endsWith("DURATION") || s.endsWith("DUR")) {
            return NamedEntityType.DURATION;
        }
        if(s.endsWith("GPE")){
            return GEOGRAPHICAL_ENTITY;
        }
        return NamedEntityType.MISC;
    }

}
