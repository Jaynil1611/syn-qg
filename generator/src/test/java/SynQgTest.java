import net.synqg.qg.implicative.PhrasalImplicativesResource;
import net.synqg.qg.implicative.SimpleImplicativesResource;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.labels.DependencyLabel;
import net.synqg.qg.service.DefaultQgAnalysisService;
import net.synqg.qg.service.GeneratedQuestion;
import net.synqg.qg.service.IQgAnalysis;
import net.synqg.qg.service.QgAnalysisService;
import net.synqg.qg.service.SynQGService;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.synqg.qg.implicative.EntailedPolarity.DID_NOT_HAPPEN;
import static net.synqg.qg.implicative.EntailedPolarity.HAPPENED;

/**
 * Test cases for SynQG.
 *
 * @author kaustubhdholé.
 */
public class SynQgTest {

    private static SynQGService questionGenerator;
    private static SentenceDetectorME sentenceSplitter;
    private static QgAnalysisService qgAnalysisService;

    @BeforeClass
    public static void load() {
        questionGenerator = new SynQGService(true, false, false, true);
        sentenceSplitter = sentenceSplitter();
        qgAnalysisService = new DefaultQgAnalysisService();
    }

    @Test
    public void testDOBJTemplate() {
        List<String> questions = questionGenerator.generateQuestionAnswers(Collections.singletonList(
                "John killed Mary."))
                .stream()
                .map(q -> q.preCorrection())
                .collect(Collectors.toList());
        assert questions.size() == 3;
        assert questions.contains("Whom did John kill ?")
                && questions.contains("Who killed Mary ?")
                && questions.contains("Did John kill Mary ?");
    }

    @Test
    public void testDOBJTemplateAmCausalTemplate() {
        List<String> questions = questionGenerator
                .generateQuestionAnswers(Collections.singletonList(
                        "The world crushed Rambo because of his guts at display every afternoon."))
                .stream()
                .map(q -> q.preCorrection())
                .collect(Collectors.toList());

        assert questions.contains("Whom did The world crush because of his guts at display every afternoon ?") //dobj
                // After BT:Who did The World crush every afternoon because of his bravery on show?
                && questions.contains("What crushed Rambo because of his guts at display every afternoon ?") //dobj
                // After BT: What crushed Rambo because his courage was such that he could parade every afternoon?
                && questions.contains("Did The world crush Rambo because of his guts at display every afternoon ?") //dobj
                // Did the world exterminate Rambo for his courage every afternoon?
                && questions.contains("Why did The world crush Rambo ?"); // causal
    }

    @Test
    public void testDOBJAndWordNetAndXCOMP() {

        List<String> questions = questionGenerator
                .generateQuestionAnswers(Collections.singletonList(
                        "John made a cake to eat."))
                .stream()
                .map(q -> q.preCorrection())
                .collect(Collectors.toList());

        assert questions.contains("What did John make ?"); // DobjTemplate
        // After BT: What did John do?
        assert questions.contains("Who made a cake to eat ?"); // DobjTemplate
        // After BT: Who has a cake to eat? (Slightly incorrect)
        assert questions.contains("Did John make a cake to eat ?");// DobjTemplate
        assert questions.contains("Which baked goods did John make ?"); // DobjTemplate (with WN)
        // After BT: What baked goods did John produce?
        assert questions.contains("What did John make ?");// xcomp
        assert questions.contains("Who made a cake to eat ?"); // xcomp
        assert questions.contains("Did John make a cake to eat ?");// xcomp
    }

    @Test
    public void testAmLocTemplate() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator
                .generateQuestionAnswers(Collections.singletonList(
                        "Given the desperate circumstances in New York, John killed Mary near the garden at 8 o'clock. "));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .collect(Collectors.toList());

        //assert questions.contains("Whom did John kill given the desperate circumstances in New York at 8 o'clock near the garden ?");//dobj
        assert questions.contains("Whom did John kill given the desperate circumstances in New York near the garden at 8 o'clock ?");//dobj
        assert questions.contains("Did John kill Mary given the desperate circumstances in New York near the garden at 8 o'clock ?");//dobj
        assert questions.contains("Where did John kill Mary given the desperate circumstances in New York at 8 o'clock ?"); // AMLOC
        assert questions.contains("Who killed Mary near the garden ?"); //AMLOC
        assert questions.contains("Did John kill Mary near the garden given the desperate circumstances in New York at 8 o'clock ?"); //AMLOC
        assert questions.contains("When did John kill Mary given the desperate circumstances in New York near the garden ?"); //AMLOC
        assert questions.contains("Who killed Mary given the desperate circumstances in New York near the garden at 8 o'clock ?");//dobj
    }

    @Test
    public void testAmExtTemplate() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator
                .generateQuestionAnswers(Collections.singletonList(
                        "Bojack Horseman won the 2018 general elections by a margin of 15%"));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .collect(Collectors.toList());
        assert questions.contains("What did Bojack Horseman win by a margin of 15 % ?");//dobj
        assert questions.contains("Who won the 2018 general elections by a margin of 15 % ?");
        // After BT: Who won the 2018 general election by a 15% margin?
        assert questions.contains("Did Bojack Horseman win the 2018 general elections by a margin of 15 % ?");
        assert questions.contains("Which predestination did Bojack Horseman win by a margin of 15 % ?");
        assert questions.contains("Which space did Bojack Horseman win the 2018 general elections by ?");
        assert questions.contains("By how much did Bojack Horseman win the 2018 general elections ?");// AM-EXT
        assert questions.contains("Bojack Horseman won the 2018 general elections by a margin of how much percent ?"); // Generic-NER
        assert questions.size() == 9;
    }

    @Test
    public void testAmDirectionAndManner() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator
                .generateQuestionAnswers(Collections.singletonList(
                        "The liquid was oozing out of the chamber at mind blowing speeds."));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .collect(Collectors.toList());
        assert questions.contains("In which direction was The liquid oozing at mind blowing speeds ?"); // AM-DIR
        assert questions.contains("How was The liquid oozing out of the chamber ?"); // AM-MNR
        assert questions.contains("Was The liquid oozing at mind blowing speeds ?"); // PobjTemplate::BOOLEAN
        assert questions.contains("What was oozing at mind blowing speeds ?"); // PobjTemplate::SUBJECT-BASED
    }

    @Test
    public void testAmCausalAndManner() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(
                Collections.singletonList("Allen cured the deer since the animal was shrieking in pain."));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());

        assert questions.contains("What did Allen cure since the animal was shrieking in pain ?");
        assert questions.contains("Who cured the deer since the animal was shrieking in pain ?");
        assert questions.contains("Did Allen cure the deer since the animal was shrieking in pain ?"); //DOBJ
        assert questions.contains("Which ruminant did Allen cure since the animal was shrieking in pain ?"); // DOBJ-WordNet check
        assert questions.contains("What was shrieking in pain ?");
        assert questions.contains("Was the animal shrieking in pain ?");
        assert questions.contains("Which hurt was the animal shrieking in ?");// Not a great WN question
        assert questions.contains("Why did Allen cure the deer ?"); //AM-CAU
        assert questions.contains("How was the animal shrieking ?"); //AM-MNR

        assert generatedQuestions.size() == 9;
        assert questions.size() == 9;
    }

    @Test
    public void testAmTemporal() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(
                Collections.singletonList("The city of Winterfell did not get freedom until the end of summer."));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());

        assert questions.contains("Till when did The city of Winterfell not get freedom ?"); //AM-TMP untill
        // After BT: Until when did the city of Winterfell not get freedom?
        assert questions.contains("What did The city of Winterfell not get until the end of summer ?"); //DOBJ
        // After BT: What did the town of Winterfell get towards the end of the summer? (Slight change)
        assert questions.contains("What did not get freedom until the end of summer ?"); // DOBJ
        assert questions.contains("Did The city of Winterfell get freedom until the end of summer ?"); //DOBJ negative boolean
        assert questions.contains("Which state did The city of Winterfell not get until the end of summer ?"); // DOBJ- WordNet
        assert questions.contains("The city of which place did not get freedom until the end of summer ?");

        assert questions.size() == 7;
        assert generatedQuestions.size() == 9;
    }

    @Test
    public void testPOBJAndAmTmp() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(Collections.singletonList(
                "The distinct cultural and ethnic identity of the Normans emerged initially in the first half of the 10th century, and it continued to evolve over the succeeding centuries."));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());

        assert questions.contains("When did it evolve ?");
        assert questions.contains("When did The distinct cultural and ethnic identity of the Normans emerge ?");
        // After BT: When did the cultural and ethnic identity of the Normans come into being?
        assert questions.contains("Which large integer did it evolve over ?");
        assert questions.contains("Did it evolve over the succeeding centuries ?");
        assert questions.contains("What evolves over the succeeding centuries ?");
        assert questions.contains("Which common fraction did The distinct cultural and ethnic identity of the Normans emerge in ?");
        assert questions.contains("Did The distinct cultural and ethnic identity of the Normans emerge in the first half of the 10th century ?");
        assert questions.contains("What emerged in the first half of the 10th century ?");
        assert generatedQuestions.size() == 9;
        assert questions.size() == 8;
        // The below two questions need changing in the NER template.
        // TODO: if the NE type is in front of a noun, then the question should change.
    }


    @Test
    public void testMany() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(
                Collections.singletonList("John and Kevin played basketball for 12 months in the backyard of New York."));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());
        assert questions.contains("What did John and Kevin play in the backyard of New York for 12 months ?");
        assert questions.contains("Who played basketball in the backyard of New York for 12 months ?");
        assert questions.contains("Did John and Kevin play basketball in the backyard of New York for 12 months ?");
        assert questions.contains("Which ball did John and Kevin play in the backyard of New York for 12 months ?");
        assert questions.contains("Who played basketball for 12 months ?");
        assert questions.contains("Did John and Kevin play basketball for 12 months ?");
        assert questions.contains("Which time unit did John and Kevin play basketball for ?");
        assert questions.contains("Who played basketball in the backyard of New York ?");
        assert questions.contains("Did John and Kevin play basketball in the backyard of New York ?");
        assert questions.contains("Which yard did John and Kevin play basketball in ?");
        // After BT: In which courtyard did John and Kevin play basketball?
        assert questions.contains("When did John and Kevin play basketball in the backyard of New York ?");
        assert questions.contains("Where did John and Kevin play basketball for 12 months ?");
        assert questions.contains("John and Kevin played basketball for 12 months in the backyard of which place ?"); // NER-LOCATION
        assert questions.size() == 13;
        assert generatedQuestions.size() == 15;
    }

    @Test
    public void testNer() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(
                Collections.singletonList("Three people from India went to Africa for seven years."));
        // TODO: "Three people from the US went to Africa for seven years." (can leave it for BT too)
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());
        assert questions.contains("What went to Africa ?");
        assert questions.contains("Did Three people from India go to Africa ?");
        assert questions.contains("What went for seven years ?");//POBJ Subj
        assert questions.contains("Did Three people from India go for seven years ?");
        assert questions.contains("Which time period did Three people from India go for ?");
        assert questions.contains("When did Three people from India go to Africa ?");
        assert questions.contains(" how many people from India went to Africa for seven years ?");
        assert questions.contains("Three people from which place went to Africa for seven years ?");
        assert questions.size() == 8;
    }

    @Test
    public void testMoney() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(
                Collections.singletonList("Jacob paid 100 dollars to Johny."));
        // TODO: "Three people from the US went to Africa for seven years." (can leave it for BT too)
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());
        assert questions.contains("What did Jacob pay to Johny ?");
        assert questions.contains("Which symbol did Jacob pay to Johny ?");
        assert questions.contains("Who paid 100 dollars to Johny ?");
        assert questions.contains("Did Jacob pay 100 dollars to Johny ?");
        assert questions.contains("Jacob paid how much money to Johny ?"); // money ner
        assert generatedQuestions.size() == 7;
        assert questions.size() == 5;
    }

    @Test
    public void testHowMany() {
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(
                Collections.singletonList("The world needs 10 people for the job."));
        // TODO: "Three people from the US went to Africa for seven years." (can leave it for BT too)
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());
        assert questions.contains("What does The world need for the job ?");
        assert questions.contains("What needs 10 people for the job ?");
        assert questions.contains("Does The world need 10 people for the job ?");
        assert questions.contains("Which group does The world need for the job ?");
        assert questions.contains("Which difficulty does The world need 10 people for ?");
        assert questions.contains("For what purpose does The world need 10 people ?");
        assert questions.contains("The world needs how many people for the job ?");// How many
        assert questions.size() == 7;
        assert generatedQuestions.size() == 9;
    }

    @Test
    public void simpleImplicativeTest() {
        SimpleImplicativesResource simpleImplicativesResource = new SimpleImplicativesResource();
        List<String> sentences = new ArrayList<>();
        sentences.add("The man failed to pay $100.");
        sentences.add("The man never failed to pay his debt.");
        sentences.add("Ed woke to find that breakfast had arrived.");
        List<IQgAnalysis> qgAnalyses = qgAnalysisService.parse(sentences);

        DependencyNode mainClauseVerb = oneWithChild(qgAnalyses.get(0), DependencyLabel.XCOMP);
        assert mainClauseVerb.form().equalsIgnoreCase("failed");
        assert simpleImplicativesResource.getEntailedPolarity(mainClauseVerb).get() == DID_NOT_HAPPEN;

        mainClauseVerb = oneWithChild(qgAnalyses.get(1), DependencyLabel.XCOMP);
        assert mainClauseVerb.form().equalsIgnoreCase("failed");
        assert simpleImplicativesResource.getEntailedPolarity(mainClauseVerb).get() == HAPPENED;

        mainClauseVerb = oneWithChild(qgAnalyses.get(2), DependencyLabel.XCOMP);
        assert mainClauseVerb.form().equalsIgnoreCase("woke");
        assert simpleImplicativesResource.getEntailedPolarity(mainClauseVerb).get() == HAPPENED;
    }

    @Test
    public void phrasalImplicativeTest() {
        PhrasalImplicativesResource phrasalImplicativesResource = new PhrasalImplicativesResource();
        List<String> sentences = new ArrayList<>();
        sentences.add("Kim didn't make any attempt to show her feelings.");
        sentences.add("Napoleon did not take the trouble to study the country he was going to invade.");
        List<IQgAnalysis> qgAnalyses = qgAnalysisService.parse(sentences);
        System.out.println();

        // Kim didn't show her feelings.
        DependencyNode mainClauseVerb = oneWithChild(qgAnalyses.get(0), DependencyLabel.DOBJ);
        assert mainClauseVerb.form().equalsIgnoreCase("make");
        DependencyNode mainClauseNoun = mainClauseVerb.childrenWithDepLabel(DependencyLabel.DOBJ).get(0);
        assert mainClauseNoun.form().equalsIgnoreCase("attempt");
        assert phrasalImplicativesResource.getEntailedPolarity(mainClauseVerb, mainClauseNoun).get() == DID_NOT_HAPPEN;

        // Napoleon didn’t study the country he was going to invade.
        mainClauseVerb = oneWithChild(qgAnalyses.get(1), DependencyLabel.DOBJ);
        assert mainClauseVerb.form().equalsIgnoreCase("take");
        mainClauseNoun = mainClauseVerb.childrenWithDepLabel(DependencyLabel.DOBJ).get(0);
        assert mainClauseNoun.form().equalsIgnoreCase("trouble");
        assert phrasalImplicativesResource.getEntailedPolarity(mainClauseVerb, mainClauseNoun).get() == DID_NOT_HAPPEN;
    }

    @Test
    public void testPhrasalImplicativeQuestions() {
        String sentence = "John didn't have the courage to pay Sam $100 for the car.";
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(
                Collections.singletonList(sentence));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());
        List<String> answers = generatedQuestions.stream()
                .map(q -> q.shortAnswer())
                .collect(Collectors.toList());

        assert generatedQuestions.size() == 7;
        assert questions.contains("What did John not have ?"); // dobj obj
        assert questions.contains("Who did not have the courage to pay Sam $ 100 for the car ?");// dobj sub
        assert questions.contains("Did John have the courage to pay Sam $ 100 for the car ?"); // dobj boolean
        assert questions.contains("Which spirit did John not have ?");// dobj wordnet
        assert questions.contains("Do John pay $ 100 Sam ?"); // TODO: parent (DoBJ) --> parent verb

        assert Collections.frequency(answers, "No") == 3;
    }


    @Test
    public void testSimpleImplicativeQuestions() {
        String sentence = "John failed to kill Mary in the garden.";
        List<GeneratedQuestion> generatedQuestions = questionGenerator.generateQuestionAnswers(
                Collections.singletonList(sentence));
        List<String> questions = generatedQuestions.stream()
                .map(q -> q.preCorrection())
                .distinct()
                .collect(Collectors.toList());
        List<String> answers = generatedQuestions.stream()
                .map(q -> q.shortAnswer())
                .collect(Collectors.toList());

        assert generatedQuestions.size() == 5;
        assert questions.contains("What did John fail ?"); // dobj obj
        assert questions.contains("Who failed to kill Mary in the garden ?");// dobj sub
        assert questions.contains("Did John fail to kill Mary in the garden ?");// Yes // dobj boolean
        assert questions.contains("Did John kill Mary in the garden ?");// No // dobj wordnet

        assert Collections.frequency(answers, "No") == 2;
        assert Collections.frequency(answers, "Yes") == 1;
    }

    private DependencyNode oneWithChild(IQgAnalysis qgAnalysis, DependencyLabel dependencyLabel) {
        return qgAnalysis
                .getSentenceParse()
                .dependencyNodes()
                .stream()
                .filter(d -> d.childrenWithDepLabel(dependencyLabel).stream().count() >= 1)
                .findFirst().get();
    }

    private static SentenceDetectorME sentenceSplitter() {
        SentenceDetectorME detector;
        try {
            InputStream inputStream = new FileInputStream("src/main/resources/tokenizer/en-sent.bin");
            SentenceModel model = new SentenceModel(inputStream);
            detector = new SentenceDetectorME(model);
        } catch (Exception ex) {
            System.err.println("Couldn't load the sentence splitter");
            detector = null;
        }
        return detector;
    }

}

// file list:
// "/Users/kdhole/Downloads/dev-v2.0-all-2.json"
// "/Users/kdhole/Downloads/dev-v2.0-all-3.json" --> has the context split into individual sentences.
// "/Users/kdhole/Downloads/dev-v2.0-all-thematic.json" --> contains even the individual source sentence
// "/Users/kdhole/Downloads/dev-v2.0-all-thematic-copy.json"

//  "/Users/kdhole/Downloads/dev-v2.0-all-thematic-bt.json" --> contains BT version of thematic sentences (propBank descriptions).
// "/Users/kdhole/Downloads/dev-v2.0-all-them-pred.json" -->
// "/Users/kdhole/Downloads/dev-v2.0-all-them-pred-utf.json"
//"/Users/kdhole/Downloads/dev-v2.0-all-them-pred-clean.json"
// "/Users/kdhole/Downloads/dev-v2.0-all-them-pred-clean-complete.json"
// "/Users/kdhole/Downloads/dev-v2.0-all-them-pred-clean-complete.json" --> bt-3-trans.csv, bt-2-trans.csv
// "/Users/kdhole/Downloads/dev-v2.0-all-them-pred-clean-complete-2.json" --> bt-9.csv (them/pred is left)
// /Users/kdhole/Downloads/dev-v2.0-all-them-pred-clean-complete-2-copy.json
// "/Users/kdhole/Downloads/dev-v2.0-all-them-pred-clean-complete-3-copy.json" --> bt-15,16,17,18,19
// // "/Users/kdhole/Downloads/dev-v2.0-all-them-pred-clean-complete-3-copy copy.json" --> bt-15,16,17,18,19
// "/Users/kdhole/Downloads/finalOne.json" --> this still has some incomplete backtranslations