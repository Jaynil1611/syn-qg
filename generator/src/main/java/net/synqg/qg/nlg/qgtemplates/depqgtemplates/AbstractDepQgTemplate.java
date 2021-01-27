package net.synqg.qg.nlg.qgtemplates.depqgtemplates;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.synqg.qg.nlg.factories.AbstractQuestionGeneratorFactory;
import net.synqg.qg.nlg.qgtemplates.QgTemplate;
import net.synqg.qg.nlp.labels.NamedEntityType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author viswa
 */
@Getter
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractDepQgTemplate implements QgTemplate {

    AbstractQuestionGeneratorFactory.Subject subject;
    String dObject;
    AbstractQuestionGeneratorFactory.Verb verb;
    String pObject;
    String extraField;
    NamedEntityType namedEntityType;
    String hypernym;

    String trigger;

    public String dObject() {
        return dObject;
    }

    public void dObject(String object) {
        this.dObject = object;
    }
}
