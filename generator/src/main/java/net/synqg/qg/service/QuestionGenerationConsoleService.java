package net.synqg.qg.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

import static net.synqg.qg.utils.SynQgUtils.*;

/**
 * Service which generates questions from the
 *
 * @author kaustubhdholé.
 */
public class QuestionGenerationConsoleService {

    private static SynQGService questionGenerator;
    private static boolean printLogs = true;

    public static void run() {
        questionGenerator = new SynQGService(true, true, false, printLogs);
        System.out.println("Ensure that you have the NLP server and the back-translation server on.");
        while (true) {
            System.out.println("Type a sentence to generate questions.");
            //Enter data using BufferReader
            BufferedReader console =
                    new BufferedReader(new InputStreamReader(System.in));

            // Reading data using readLine
            String sentence = null;
            try {
                sentence = console.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            questionGenerator.generateQuestionAnswers(Collections.singletonList(sentence))
                    .forEach(q -> {
                        if (printLogs) {
                            System.out.print(ANSI_YELLOW + q.prettyPrint() + ANSI_RESET);
                            System.out.println("After BackTranslation:");
                        }
                        System.out.println("\t" + ANSI_BLUE + q.question() + ANSI_RESET);
                        System.out.println();
                    });

        }
    }

    public static void main(String[] args) {
        run();
    }
}
