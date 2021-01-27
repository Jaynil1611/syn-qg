package net.synqg.qg.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.synqg.qg.service.DefaultQgAnalysisService;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class WordToNounConverter {

    private DefaultQgAnalysisService defaultQgAnalysisService;

    public List<String> convertToNoun(String word) {
        try {
            String verbs = defaultQgAnalysisService.getJson(word, "nounify");
            return new ArrayList<>();
        } catch (Exception ex) {
            log.warn("Couldn't convert {} to its noun form.", word);
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        DefaultQgAnalysisService defaultQgAnalysisService = new DefaultQgAnalysisService();
        WordToNounConverter wordToNounConverter = new WordToNounConverter(defaultQgAnalysisService);
        wordToNounConverter.convertToNoun("appear");
        wordToNounConverter.convertToNoun("eat");
        wordToNounConverter.convertToNoun("apply");
        wordToNounConverter.convertToNoun("create");
    }

}
