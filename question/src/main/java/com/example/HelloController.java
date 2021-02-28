package com.example;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import net.synqg.qg.service.GeneratedQuestion;
import net.synqg.qg.service.SynQGService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Controller("/api/v1")
public class HelloController {
    Logger logger = Logger.getLogger(HelloController.class.getName());
    @Get(value="/questions/params")
    @Produces(MediaType.APPLICATION_JSON)
    public  List<String> question(@QueryValue String searchQuery) {
        SynQGService synQGService = new SynQGService();
        logger.info(searchQuery);
        String input = "John failed to kill Mary.";
        List<String> results = new ArrayList<String>();
        List<GeneratedQuestion> questions = synQGService.generateQuestionAnswers(Collections.singletonList(searchQuery));
        for (GeneratedQuestion generatedQuestion : questions) {
            String outline = "";
            outline = outline + generatedQuestion.question() + "\t";
            outline = outline + generatedQuestion.shortAnswer() + "\t";
            outline = outline + generatedQuestion.templateName() + "\t";
            outline = outline + input;
            results.add(outline);
        }
        return results;
    }
}
