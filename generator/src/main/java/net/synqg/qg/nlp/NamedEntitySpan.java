package net.synqg.qg.nlp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.synqg.qg.nlp.labels.NamedEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Named Entity span of depNodes.
 *
 * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode
public class NamedEntitySpan {

    private NamedEntityType type;
    private List<DependencyNode> tokens;
    private int startIndex;
    private int endIndex;

    public NamedEntitySpan() {
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
