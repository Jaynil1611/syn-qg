package net.synqg.qg.nlg.qgtemplates.nerqgtemplates;

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
public abstract class AbstractNerQgTemplate implements QgTemplate {

    String subject;
    String object;
    AbstractQuestionGeneratorFactory.Verb verb;
    String extraField;
    String answer;
    Boolean isSubject;

    String trigger;

}
