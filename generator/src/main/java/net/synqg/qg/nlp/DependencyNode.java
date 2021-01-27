package net.synqg.qg.nlp;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.synqg.qg.nlp.labels.DependencyLabel;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.nlp.labels.PosLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic wrapper for a dependency node for a tree.
 */
@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class DependencyNode {

    private String form;

    private String lemma;

    private PosLabel pos;

    private DependencyLabel depLabel;

    private NamedEntityType namedEntityType;

    private List<DependencyNode> children = new ArrayList<>();

    private DependencyNode head;

    public ImmutableList<DependencyNode> childrenWithDepLabel(final DependencyLabel... labels) {
        return children.stream().filter(child -> Arrays.asList(labels).contains(child.depLabel()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    @Override
    public String toString() {
        return form + "  "+ depLabel;
    }
}
