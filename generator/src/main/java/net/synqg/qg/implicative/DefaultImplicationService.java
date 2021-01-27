package net.synqg.qg.implicative;

import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.labels.DependencyLabel;

import java.util.Optional;

/**
 * Default implementation of {@link ImplicationService}
 *
 * @author kaustubhdholé.
 */
public class DefaultImplicationService implements ImplicationService {

    private SimpleImplicativesResource simpleImplicativesResource;
    private PhrasalImplicativesResource phrasalImplicativesResource;

    public DefaultImplicationService() {
        simpleImplicativesResource = new SimpleImplicativesResource();
        phrasalImplicativesResource = new PhrasalImplicativesResource();
    }

    @Override
    public Optional<EntailedPolarity> computeEntailedPolarity(DependencyNode currentClause) {
        Optional<DependencyNode> parentClauseVerb = getParentClauseVerb(currentClause);
        if (parentClauseVerb.isPresent()) {
            Optional<DependencyNode> parentClauseDobj = getObject(parentClauseVerb.get());
            if (parentClauseDobj.isPresent()) {
                // John lacked an opportunity to -pay- Mary.
                return phrasalImplicativesResource
                        .getEntailedPolarity(parentClauseVerb.get(), parentClauseDobj.get());

            } else {
                // John failed to -pay- Mary. John was reluctant to -cancel- his policy.
                return simpleImplicativesResource
                        .getEntailedPolarity(parentClauseVerb.get());
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean computeEntailedPolarityOrDefault(DependencyNode dependencyNode, boolean defaultPolarity) {
        Optional<EntailedPolarity> entailedPolarity1 = computeEntailedPolarity(dependencyNode);
        if (entailedPolarity1.isPresent()) {
            return entailedPolarity1.get().hasHappened();
        }
        return defaultPolarity;
    }

    private Optional<DependencyNode> getParentClauseVerb(DependencyNode currentVerb) {
        if (currentVerb.head() != null) {
            if (currentVerb.head().pos().isVerb()) {
                return Optional.of(currentVerb.head());
            } else if (currentVerb.head().head() != null
                    && currentVerb.head().head().pos().isVerb()) {
                // John lacked an opportunity to pay Mary $100.
                // Here, pay has a verb at its grandparent level.
                return Optional.of(currentVerb.head().head());
            }
        }
        return Optional.empty();
    }

    private Optional<DependencyNode> getObject(DependencyNode node) {
        return node.childrenWithDepLabel(DependencyLabel.DOBJ, DependencyLabel.OBJ)
                .stream()
                .findFirst();
    }
}
