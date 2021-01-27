package net.synqg.qg.nlp;

import com.google.common.collect.ImmutableList;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic Dependency Parse :
 * Default --> AllenNLP Parsed Dependency Structure of a sentence.
 */
@Data
@Accessors(fluent = true)
public class SentenceParse {

    private List<DependencyNode> dependencyNodes;

    private String sentenceText;

    public ImmutableList<DependencyNode> depNodes() {
        return ImmutableList.<DependencyNode>builder().build();
    }

    public String tokenizedText() {
        return dependencyNodes.stream()
                .map(t -> t.form())
                .collect(Collectors.joining(" "));
    }

}
