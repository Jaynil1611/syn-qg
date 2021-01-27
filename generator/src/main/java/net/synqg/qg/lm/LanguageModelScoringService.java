package net.synqg.qg.lm;

import java.io.IOException;

/**
 * Language Model Service which sends sentence and
 * gets LM loss for the sentence.
 *
 * @author kaustubhdholé.
 */
public interface LanguageModelScoringService {

    /**
     * sentence --> LM loss
     *
     * @param sentence Donald Trump traveled to the Gulf to discuss the next steps.
     * @return 1.5308409902232287
     * @throws IOException
     */
    Float getLanguageModelLoss(String sentence) throws IOException;
}
