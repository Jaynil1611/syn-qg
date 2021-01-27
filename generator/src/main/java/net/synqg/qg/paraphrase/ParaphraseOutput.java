package net.synqg.qg.paraphrase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;


/**
 * List of filtered paraphrases.
 *
 * @author kaustubhdholé.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@Accessors(fluent = true)
public class ParaphraseOutput {

    List<Paraphrase> phrases;

    String queryPhrase;

    List<String> posFilter;

    public String toString() {
        return "Query = " + queryPhrase + "\n"
                + "Paraphrases = \n"
                + phrases()
                .stream().map(p -> "POS: " + p.lhs() + " Target: " + p.target())
                .collect(Collectors.joining("\n"));
    }
}