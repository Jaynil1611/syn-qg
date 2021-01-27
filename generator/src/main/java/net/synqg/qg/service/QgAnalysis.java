package net.synqg.qg.service;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.synqg.qg.nlp.NamedEntitySpan;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.nlp.SentenceParse;
import net.synqg.qg.nlp.SemanticRoleList;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of {@link IQgAnalysis}
 *
 * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
public class QgAnalysis implements IQgAnalysis {

    private SentenceParse sentenceParse;
    private List<SemanticRoleList> srlSpans;
    private Set<NamedEntitySpan> namedEntitySpans;
    private Map<String, NamedEntityType> namedEntities;
    private Map<String, NamedEntityType> fineGrainedNamedEntities;

    public SentenceParse getSentenceParse() {
        return sentenceParse;
    }

    @Override
    public List<SemanticRoleList> getSrlSpans() {
        return srlSpans;
    }

    @Override
    public Set<NamedEntitySpan> getNamedEntitySpans() {
        return namedEntitySpans;
    }

    public Map<String, NamedEntityType> getNerLabels() {
        return namedEntities;
    }

    @Override
    public Map<String, NamedEntityType> getFineGrainedNerLabels() {
        return fineGrainedNamedEntities;
    }

    @Override
    public String toString() {
        return "sentenceParse " + sentenceParse.toString() + "\n"
                + "Semantic Role Labels " + srlSpans.toString() + "\n"
                + "Named Entity Map " + namedEntities.toString() + "\n"
                + "Fine Grained Named Entity Map " + fineGrainedNamedEntities.toString();
    }
}
