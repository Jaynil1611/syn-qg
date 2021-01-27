package net.synqg.qg.nlg.qgtemplates.srlqgtemplates;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.synqg.qg.nlg.factories.AbstractQuestionGeneratorFactory;
import net.synqg.qg.nlg.qgtemplates.QgTemplate;

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
public abstract class AbstractSrlQgTemplate implements QgTemplate {

    AbstractQuestionGeneratorFactory.Subject subject;
    String object;
    AbstractQuestionGeneratorFactory.Verb verb;
    String extraField;
    String semanticText;
    String trigger;
}
