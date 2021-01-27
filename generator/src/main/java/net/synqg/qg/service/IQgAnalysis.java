package net.synqg.qg.service;


import net.synqg.qg.nlp.NamedEntitySpan;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.nlp.SentenceParse;
import net.synqg.qg.nlp.SemanticRoleList;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author viswa
 */
public interface IQgAnalysis {

    SentenceParse getSentenceParse();

    List<SemanticRoleList> getSrlSpans();

    Set<NamedEntitySpan> getNamedEntitySpans();

    Map<String, NamedEntityType> getNerLabels();

    Map<String, NamedEntityType> getFineGrainedNerLabels();

}
