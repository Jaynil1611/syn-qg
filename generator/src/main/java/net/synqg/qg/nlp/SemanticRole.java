package net.synqg.qg.nlp;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.synqg.qg.nlp.labels.SemanticRoleLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An (SRL) span of depNodes.
 *
 * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
public class SemanticRole {

    private SemanticRoleLabel type;
    private List<DependencyNode> tokens;

    public SemanticRole() {
        tokens = new ArrayList<>();
    }

    public String form() {
        return tokens.stream()
                .map(t -> t.form())
                .collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        return "[ " + type + " " + form() + " ]";
    }
}
