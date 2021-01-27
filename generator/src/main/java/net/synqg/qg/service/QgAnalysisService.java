package net.synqg.qg.service;

import java.util.List;

/**
 * Parse the sentence into their dependency, SRL and NER representations.
 *
 * @author kaustubhdholé.
 */
public interface QgAnalysisService {

    /**
     * Parse the input string to get the relavant parameters.
     *
     * @param sentences input utterance
     * @return IQgAnalysis which contains the SRL, Dependency Parse and the detected named entities of the input sentences.
     */
    List<IQgAnalysis> parse(List<String> sentences);

}
