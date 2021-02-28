package net.synqg.qg.service;

import net.synqg.qg.service.GeneratedQuestion;
import net.synqg.qg.service.SynQGService;
import java.util.Collections;
import java.util.List;

public class SynQgClient {

    public static void main(String[] args) {
        SynQGService synQGService = new SynQGService();
        String input = "John failed to kill Mary.";
        List<GeneratedQuestion> questions = synQGService.generateQuestionAnswers(Collections.singletonList(input));
        for (GeneratedQuestion generatedQuestion : questions) {
            String outline = "";
            outline = outline + generatedQuestion.question() + "\t";
            outline = outline + generatedQuestion.shortAnswer() + "\t";
            outline = outline + generatedQuestion.templateName() + "\t";
            outline = outline + input;
            System.out.println(outline);
        }
    }
}