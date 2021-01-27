package net.synqg.qg.nlp.normalizer;

import net.synqg.qg.utils.FileOperationUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Normalize "I won't" --> "I would not."
 *
 * @author kaustubhdholé.
 */
public class TextNormalizer implements Function<String, String> {

    private Map<String, String> replacements = new HashMap<>();
    private static final String SPACE = " ";

    public TextNormalizer() {
        String path = this
                .getClass()
                .getClassLoader()
                .getResource("net/synqg/qg/nlp/normalizer/substitutions.txt")
                .getPath();
        FileOperationUtils.readLines(path)
                .stream()
                .forEach(line -> {
                    String[] splits = line.split("_");
                    String unnormalized = splits[0];
                    String normalized = splits[1];
                    replacements.put(unnormalized, normalized);
                });
    }

    @Override
    public String apply(String sentence) {
        return Arrays.stream(sentence.split(SPACE))
                .map(s -> replacements.getOrDefault(s, s))
                .collect(Collectors.joining(SPACE));
    }
}
