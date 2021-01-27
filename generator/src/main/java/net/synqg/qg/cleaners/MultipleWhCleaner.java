package net.synqg.qg.cleaners;

import net.synqg.qg.service.GeneratedQuestion;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filters out questions with multiple Wh.
 *
 * @author kaustubhdholé.
 */
public class MultipleWhCleaner implements QuestionCleaner {

    private static final List<String> whWords = Arrays.asList("where", "which", "who", "when", "why", "how", "what");

    @Override
    public Map<GeneratedQuestion, String> apply(Map<GeneratedQuestion, String> questionAnswerPairs) {
        return questionAnswerPairs.entrySet()
                .stream()
                .filter(q -> singleWh(q.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean singleWh(GeneratedQuestion question) {
        String[] words = question.question().toLowerCase().split(" ");
        long count = Arrays.stream(words)
                .filter(w -> whWords.contains(w))
                .count();
        return count <= 1;
    }
}