package net.synqg.qg.paraphrase;

import java.util.List;

/**
 * Paraphrase Service.
 *
 * @author kaustubhdholé.
 */
public interface ParaphraseService {

    ParaphraseOutput generate(String query);

    ParaphraseOutput generate(String query, List<String> pos);
}
