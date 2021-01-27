package net.synqg.qg.nlg.factories;

import net.synqg.qg.nlp.labels.DependencyLabel;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.nlp.labels.SemanticRoleLabel;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.SentenceParse;
import net.synqg.qg.nlp.SemanticRoleList;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author viswa
 */
public class QuestionGeneratorHelper {

    public static Map<String, String> pronounMap = new HashMap<>();

    public static Map<String, NamedEntityType> pronounNerMap = new HashMap<>();

    static {
        pronounMap.put("she", "her");
        pronounMap.put("he", "him");
        pronounMap.put("you", "him");
        pronounMap.put("we", "them");
        pronounMap.put("i", "me");

        pronounNerMap.put("she", NamedEntityType.PERSON);
        pronounNerMap.put("he", NamedEntityType.PERSON);
        pronounNerMap.put("they", NamedEntityType.PERSON);
        pronounNerMap.put("them", NamedEntityType.PERSON);
        pronounNerMap.put("him", NamedEntityType.PERSON);
        pronounNerMap.put("her", NamedEntityType.PERSON);
        pronounNerMap.put("i", NamedEntityType.PERSON);
        pronounNerMap.put("me", NamedEntityType.PERSON);
        pronounNerMap.put("you", NamedEntityType.PERSON);
    }

    /**
     * To get the named given the subject.
     *
     * @param subject          subject string
     * @param sentenceAnalysis Isentence analysis object
     * @return named enity type
     */
    public static NamedEntityType getNamedEntitysubject(String subject, SentenceParse sentenceAnalysis) {

        List<DependencyNode> depNodes = sentenceAnalysis.dependencyNodes();
        depNodes = depNodes.stream().filter((x -> !x.depLabel().equals(DependencyLabel.PUNCT))).collect(Collectors.toList());
        List<String> sentenceWords = depNodes.stream().map(d -> d.form().trim().toLowerCase()).collect(Collectors.toList());
        //String[] sentenceWords = sentence.trim().toLowerCase().split("\\s");
        String[] subjectWords = subject.trim().toLowerCase().split(" ");

        int sentencePointer = 0;
        int subjectPointer = 0;
        int start = 0;
        int end = 0;
        int startedSearchFrom = -1;
        int length = sentenceWords.size();
        int subjectLength = subjectWords.length;

        while (sentencePointer < length && subjectPointer < subjectLength) {

            if (sentenceWords.get(sentencePointer).equals(subjectWords[subjectPointer])) {
                // position of the first word of the subject found.
                if (startedSearchFrom == -1) {
                    startedSearchFrom = sentencePointer;
                }

                sentencePointer++;
                subjectPointer++;

                if (subjectPointer == subjectLength) {
                    end = sentencePointer;
                    start = sentencePointer - subjectLength;
                }
            } else {
                sentencePointer = ++startedSearchFrom;
                subjectPointer = 0;
            }
        }

        for (int k = start; k < end; k++) {
            DependencyNode depNode = depNodes.get(k);
            if (pronounNerMap.containsKey(depNode.form().trim().toLowerCase())) {
                return pronounNerMap.get(depNode.form().trim().toLowerCase());
            }
            if (depNode.namedEntityType() != null) {
                return depNode.namedEntityType();
            }
        }
        return null;
    }

    public static boolean containsA0orA1andOneMoreArgument(Map<SemanticRoleLabel, String> semanticRole) {
        return semanticRole.containsKey(SemanticRoleLabel.V)
                && (semanticRole.containsKey(SemanticRoleLabel.ARG0)
                || semanticRole.containsKey(SemanticRoleLabel.ARG1) && semanticRole.keySet().size() >= 3);
    }

    public static boolean containsA0orA1andOneMoreArgument(SemanticRoleList srlList) {
        boolean containsVerb = srlList.stream().anyMatch(s -> s.type().equals(SemanticRoleLabel.V));
        boolean containsA0 = srlList.stream().anyMatch(s -> s.type().equals(SemanticRoleLabel.ARG0));
        boolean containsA1 = srlList.stream().anyMatch(s -> s.type().equals(SemanticRoleLabel.ARG1));
        boolean greaterThanThree = srlList.size() >= 3;
        return containsVerb
                && (containsA0 || (containsA1 && greaterThanThree));
    }
}

