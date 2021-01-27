package net.synqg.qg.service;

import lombok.extern.slf4j.Slf4j;
import net.synqg.qg.backtranslation.BackTranslationService;
import net.synqg.qg.backtranslation.DefaultBackTranslationService;
import net.synqg.qg.cleaners.DiscourseWordsCleaner;
import net.synqg.qg.cleaners.MultipleWhCleaner;
import net.synqg.qg.cleaners.PresentationLayer;
import net.synqg.qg.cleaners.QuestionCleaner;
import net.synqg.qg.nlg.factories.QuestionGeneratorFactory;
import net.synqg.qg.nlg.qgtemplates.QgTemplate;
import net.synqg.qg.service.processors.input.CorefPreprocessor;
import net.synqg.qg.service.processors.input.Preprocessor;
import net.synqg.qg.utils.FileOperationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.synqg.qg.utils.SynQgUtils.ANSI_RESET;
import static net.synqg.qg.utils.SynQgUtils.ANSI_YELLOW;

/**
 * @author viswa
 */
//TODO: for quantmod relation, we can create a dependency question with pattern "Roughly how many ___"
@Slf4j
public class SynQGService {

    private QgAnalysisService qgAnalysisService;
    private QuestionGeneratorFactory questionGenerator;
    // TODO: put all of this in a configuration file
    private BackTranslationService backTranslationService;
    private CorefPreprocessor corefPreprocessor;
    private boolean enableCoref = false;
    private static boolean generateSquad = false;
    private boolean printLogs = false;

    private boolean enableDepSrNerGeneration;
    private boolean enableBackTranslation;
    private boolean enableThematicQuestionGeneration;
    private QuestionCleaner questionCleaner;

    public SynQGService(boolean enableDepSrNerGeneration,
                        boolean enableBackTranslation,
                        boolean enableThematicQuestionGeneration, Map<String, String> backTranslations,
                        boolean printLogs) {
        this.enableDepSrNerGeneration = enableDepSrNerGeneration;
        this.enableBackTranslation = enableBackTranslation;
        this.enableThematicQuestionGeneration = enableThematicQuestionGeneration;
        qgAnalysisService = new DefaultQgAnalysisService(printLogs);
        questionGenerator = new SequentialQuestionGenerator();
        if (enableBackTranslation) {
            backTranslationService = new DefaultBackTranslationService(backTranslations);
        } else if (!backTranslations.isEmpty()) {
            System.out.println("Back Translation has not been enabled (enableBackTranslation=false) " +
                    "but the Map of sample back-translations is not empty.");
        } else {
            System.out.println(ANSI_YELLOW + "Note that Back Translation has not been enabled (enableBackTranslation=false)!" + ANSI_RESET);
        }
        corefPreprocessor = new CorefPreprocessor();
        questionCleaner = questionCleaner();
        this.printLogs = printLogs;
    }

    public SynQGService(boolean enableDepSrNerGeneration, boolean enableBackTranslation, boolean enableThematicQuestionGeneration, boolean printLogs) {
        this(enableDepSrNerGeneration, enableBackTranslation, enableThematicQuestionGeneration, new HashMap<>(), printLogs);
    }

    public SynQGService() {
        this(true, true, false, false);
    }

    public List<GeneratedQuestion> generateQuestionAnswers(String input) {
        return generateQuestionAnswers(Collections.singletonList(input));
    }

    /**
     * Generate questions given input sentence.
     *
     * @param inputs input sentence which ends with fullstop
     * @return generated questions
     */
    public List<GeneratedQuestion> generateQuestionAnswers(List<String> inputs) {

        inputs = inputs.stream().map(input -> input.trim()).collect(Collectors.toList());

        List<GeneratedQuestion> generatedQuestions = new ArrayList<>();
        List<String> preprocessedInputs = new ArrayList<>();

        if (enableCoref) {
            List<String> corefPreprocessedInputs = new ArrayList<>();
            inputs.forEach(input -> corefPreprocessedInputs.add(corefPreprocessor.process(input)));
            corefPreprocessedInputs.forEach(input -> preprocessedInputs.add(Preprocessor.preprocess(input)));
        } else {
            inputs.forEach(input -> preprocessedInputs.add(Preprocessor.preprocess(input)));
        }

        if (enableDepSrNerGeneration) {
            List<IQgAnalysis> qgAnalyses = qgAnalysisService.parse(preprocessedInputs);

            if (qgAnalyses == null) {
                return new ArrayList<>();
            }
            // go through each of the SRL and dependency templates
            String fullParagraph = qgAnalyses.stream().map(q -> q.getSentenceParse().tokenizedText())
                    .collect(Collectors.joining(" "));
            for (IQgAnalysis qgAnalysis : qgAnalyses) {
                for (QgTemplate qgTemplate : questionGenerator.extractTemplate(qgAnalysis)) {
                    for (QaPair qaPair : qgTemplate.generateQuestion()) {
                        String question = Preprocessor.preprocess(qaPair.question());
                        GeneratedQuestion generatedQuestion = null; /*new GeneratedQuestion(question, question,
                                5.0, qaPair.answer(), qaPair.templateName(),
                                qaPair.templateUnitList().prettyPrint(), qgTemplate.trigger());*/
                        // (0) Ensure that you are not splitting the paragraph before, that the paragraph is splitting inside this function.
                        // (1) find the start and end character pointers of the answer in the text of qgAnalysis.getSentenceParse().tokenizedText()
                        // --> and store in GeneratedQuestion
                        String answer = qaPair.answer();
                        String tokenizedSentence = qgAnalysis.getSentenceParse().tokenizedText();
                        int relativeStartIndex = tokenizedSentence.indexOf(answer);
                        int indexOfThisSentence = qgAnalyses.indexOf(qgAnalysis);
                        String paragraph = null;
                        int numberOfCharactersBefore = 0;
                        if (indexOfThisSentence != 0) {
                            paragraph = qgAnalyses.subList(0, indexOfThisSentence)
                                    .stream()
                                    .map(q -> q.getSentenceParse().tokenizedText()) // this is the actual sentence entered by the user.
                                    .collect(Collectors.joining(" "));
                            numberOfCharactersBefore = paragraph.length() + 1; // +1 is for the space between 2 sentences
                        }
                        int startIndex = numberOfCharactersBefore + relativeStartIndex;
                        generatedQuestion = new GeneratedQuestion(question, question,
                                5.0, qaPair.answer(), qaPair.templateName(),
                                qaPair.templateUnitList().prettyPrint(),
                                qgTemplate.trigger(),
                                fullParagraph,
                                startIndex);
                        if (generateSquad && !checkConsistencyOfAnswerPointer(generatedQuestion)) {
                            System.out.println("Inconsistency Found : \n" + generatedQuestion.prettyPrint());
                            FileOperationUtils.writeFilesAppend("src/main/resources/data/squad/train-v1.1-artificial-mismatches.json",
                                    generatedQuestion.prettyPrint() + "\n");
                        }
                        generatedQuestions.add(generatedQuestion);
                        // (2) compute the start and end character pointers of the answer in the text of
                        // qgAnalyses.stream().map(q->q.getSentenceParse().sentenceText()).collect(Collectors.joining(" "));
                    }
                }
            }
        }

        if (enableBackTranslation) {
            // perform back-translation over each
            generatedQuestions.forEach(generatedQuestion -> {
                String originalQuestion = generatedQuestion.question();
                try {
                    String question = backTranslationService.backTranslate(originalQuestion);
                    generatedQuestion.question(question);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return generatedQuestions;
    }

    private boolean checkConsistencyOfAnswerPointer(GeneratedQuestion generatedQuestion) {
        if (generatedQuestion.shortAnswer().startsWith("Yes")
                || generatedQuestion.shortAnswer().startsWith("No")) {
            return true;
        }
        return generatedQuestion.paragraph()
                .substring(generatedQuestion.answerStartIndex())
                .startsWith(generatedQuestion.shortAnswer());
    }

    private QuestionCleaner questionCleaner() {
        List<QuestionCleaner> cleaners = new ArrayList<>();
        cleaners.add(new MultipleWhCleaner());
        cleaners.add(new DiscourseWordsCleaner());
        cleaners.add(new PresentationLayer());
        return questionAnswerPairs -> {
            for (QuestionCleaner cleaner : cleaners) {
                questionAnswerPairs = cleaner.apply(questionAnswerPairs);
            }
            return questionAnswerPairs;
        };
    }
}
