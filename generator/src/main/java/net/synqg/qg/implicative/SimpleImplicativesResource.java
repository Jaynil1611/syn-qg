package net.synqg.qg.implicative;

import lombok.extern.slf4j.Slf4j;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.utils.FileOperationUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


/**
 * Loads the simple implicatives resources which maps verbs to their implicative categories.
 *
 * @author kaustubhdholé.
 */
@Slf4j
public class SimpleImplicativesResource {

    private final Map<String, Category> verbToImplicationMap = new HashMap<>();

    private final Set<String> negatedForms;

    /**
     * Loads the verb files.
     */
    public SimpleImplicativesResource() {
        String path = Objects.requireNonNull(this
                .getClass()
                .getClassLoader()
                .getResource("net/synqg/qg/implicative/simple-implicatives.txt")
                .getPath());

        negatedForms = new HashSet<>(Arrays.asList("not", "didnt", "didnt'", "wont", "wont'", "cant",
                "cant'", "aint", "aint'", "neither", "nor", "no", "n't", "never"));

        FileOperationUtils.readLines(path)
                .forEach(l -> {
                    String[] splits = l.split("\t");
                    try {
                        verbToImplicationMap.put(splits[0], new Category(l));
                    } catch (Exception e) {
                        log.error("Unable to split line {} due to ", l, e);
                    }
                });
        log.info("Loaded Simple Implication rules for {} verbs",
                verbToImplicationMap.size());
    }

    public Optional<EntailedPolarity> getEntailedPolarity(DependencyNode mainClauseVerb) {
        String verbLemma = mainClauseVerb.lemma();
        if (verbToImplicationMap.containsKey(verbLemma)) {
            boolean mainClausePolarity = getMainClausePolarity(mainClauseVerb);
            Category category = verbToImplicationMap.get(verbLemma);
            Optional<EntailedPolarity> type = category.toEntailedPolarity(mainClausePolarity);
            return type;
        }
        return Optional.empty();
    }

    public boolean getMainClausePolarity(DependencyNode verb) {
        // (1) Polarity is positive if it has an even number of negative children like "not" and "never"

        return (verb.children()
                .stream()
                .filter(x -> negatedForms.contains(x.form().toLowerCase()))
                .count() % 2 == 0);
    }

}