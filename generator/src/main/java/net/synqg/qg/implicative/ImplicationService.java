package net.synqg.qg.implicative;

import net.synqg.qg.nlp.DependencyNode;

import java.util.Optional;

/**
 * Computes the entailed polarity based on the parent clause's polarity
 * and the nature of the clause (i.e. verb or verb+noun).
 *
 * @author kaustubhdholé.
 */
public interface ImplicationService {

    /**
     * Computes the entailed polarity of the subordinate clause
     * from the polarity and the nature of the main clause.
     *
     * @param currentClause the given verb
     */
    Optional<EntailedPolarity> computeEntailedPolarity(DependencyNode currentClause);

    /**
     * Computes the entailed polarity of the subordinate clause
     * and checks if it is positive else returns the default polarity
     *
     * @param currentClause   the given verb
     * @param defaultPolarity the polarity to return if not found  by implication service
     */
    boolean computeEntailedPolarityOrDefault(DependencyNode currentClause, boolean defaultPolarity);
}
