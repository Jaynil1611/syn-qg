package net.synqg.qg.cleaners;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.synqg.qg.service.GeneratedQuestion;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Makes the question answers presentable. (Like first word caps, etc).
 *
 * @author kaustubhdholé.
 */
public class PresentationLayer implements QuestionCleaner {

    public static final Pattern PUNCTUATION = Pattern.compile("^.+[.!?:。、！？,]$");

    @Override
    public Map<GeneratedQuestion, String> apply(Map<GeneratedQuestion, String> questionAnswerPairs) {
        Map<GeneratedQuestion, String> displayReadyPairs = new HashMap<>();
        for (Map.Entry<GeneratedQuestion, String> qaPair : questionAnswerPairs.entrySet()) {
            Pair displayReadyPair = cleanUpForDisplay(qaPair);
            displayReadyPairs.put(displayReadyPair.question(), displayReadyPair.answer());
        }
        return displayReadyPairs;
    }

    private Pair cleanUpForDisplay(Map.Entry<GeneratedQuestion, String> qaPair) {
        List<String> listWords = Arrays.stream(qaPair.getValue().split(" "))
                .map(String::toLowerCase).collect(Collectors.toList());
        String fixedAnswer = capitalizeAndAddPunctuation(String.join(" ", listWords)
                .replaceAll("^[.,!?]", ""));
        return new Pair(qaPair.getKey(), fixedAnswer);
    }

    public static String capitalizeAndAddPunctuation(String string) {
        string = StringUtils.capitalize(string).trim();
        if (!PUNCTUATION.matcher(string).find()) {
            return string + ".";
        }
        return string;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @AllArgsConstructor
    class Pair {
        private GeneratedQuestion question;
        private String answer;
    }
}