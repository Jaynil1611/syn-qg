package net.synqg.qg.nlg.qgtemplates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * One unit of template (can be a phrase or a word with the associated unit i.e. subject or label)
 *
 * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
class TemplateUnit {

    private String unit;

    private String form;

}
