package net.synqg.qg.nlp;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.synqg.qg.nlp.labels.SemanticRoleLabel;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * One set of SRL nodes (has a one to one mapping with a verb.)
 *
 * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
public class SemanticRoleList extends AbstractList<SemanticRole> {

    /**
     * The head verb of the span.
     */
    private DependencyNode verb;

    /**
     * The role arguments and their labels associated with @verb.
     */
    private List<SemanticRole> spans = new ArrayList<>();

    @Override
    public SemanticRole get(int index) {
        return spans.get(index);
    }

    public Optional<SemanticRole> get(SemanticRoleLabel semanticRoleLabel) {
        return spans.stream()
                .filter(s -> s.type().equals(semanticRoleLabel))
                .findFirst();
    }

    public List<SemanticRoleLabel> keySet() {
        return spans.stream()
                .map(s -> s.type())
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return spans.size();
    }

    public boolean add(SemanticRole span) {
        return spans.add(span);
    }

    @Override
    public String toString() {
        return spans.stream()
                .map(s -> s.toString())
                .collect(Collectors.joining(" "));
    }
}