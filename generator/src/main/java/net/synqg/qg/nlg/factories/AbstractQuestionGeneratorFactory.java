package net.synqg.qg.nlg.factories;

import lombok.AccessLevel;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import net.synqg.qg.implicative.DefaultImplicationService;
import net.synqg.qg.implicative.ImplicationService;
import net.synqg.qg.nlp.labels.DependencyLabel;
import net.synqg.qg.nlp.labels.NamedEntityType;
import net.synqg.qg.nlp.labels.PosLabel;
import net.synqg.qg.nlp.labels.SemanticRoleLabel;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.SemanticRoleList;
import net.synqg.qg.nlp.SentenceParse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author viswa
 */
@Slf4j
public abstract class AbstractQuestionGeneratorFactory implements QuestionGeneratorFactory {

    static final ImplicationService implicationService = new DefaultImplicationService();

    @Getter
    @Setter
    @Accessors(fluent = true)
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @ToString
    public static class Verb {
        String lemma;
        String mainAuxWord;
        String secondaryAuxword;
        String verb;
        String boolQuesVerb;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @ToString
    public static class Subject {
        String form;
        NamedEntityType namedEntityType;
        List<DependencyNode> tokens;

        public Subject(String form, NamedEntityType namedEntityType) {
            this.form = form;
            this.namedEntityType = namedEntityType;
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Object {
        String object;
        NamedEntityType namedEntityType;
    }

    private static Map<PosLabel, String> auxMap = new HashMap<>();

    private static final String[] auxvebs = new String[]{"is", "have", "has", "were", "was", "be",
            "did", "do", "does", "am", "had", "are",
            "will", "would", "should", "shall", "can", "could", "may", "might", "being", "been", "has", "have"};

    private static final List<String> auxVebsList = new ArrayList<>(Arrays.asList(auxvebs));

    static {
        auxMap.put(PosLabel.VB, "do");
        auxMap.put(PosLabel.VBD, "did");
        auxMap.put(PosLabel.VBN, "did");
        auxMap.put(PosLabel.VBP, "do");
        auxMap.put(PosLabel.VBZ, "does");
        auxMap.put(PosLabel.VBG, "do");
    }

    static List<SemanticRoleLabel> SEMANTIC_ROLE_LABEL_LIST = new ArrayList<SemanticRoleLabel>() {
        {
            add(SemanticRoleLabel.ARGM_CAU);
            add(SemanticRoleLabel.ARGM_PNC);
            add(SemanticRoleLabel.ARGM_MNR);
            add(SemanticRoleLabel.ARGM_LOC);
            add(SemanticRoleLabel.ARGM_TMP);
            add(SemanticRoleLabel.ARGM_PRP);
            add(SemanticRoleLabel.ARGM_DIR);
            add(SemanticRoleLabel.ARGM_EXT);
        }
    };

    private static List<SemanticRoleLabel> getSortedSemanticList(SemanticRoleList semanticRole, boolean isPrepositional) {

        List<SemanticRoleLabel> semanticRoleLabels = new ArrayList<>(semanticRole.keySet());
        List<SemanticRoleLabel> semanticRoleLabelsSub = new ArrayList<>();
        for (SemanticRoleLabel semanticRoleLabel : semanticRoleLabels) {
            if (!isPrepositional) {
                if (semanticRoleLabel.isNumberedArgument()) {
                    semanticRoleLabelsSub.add(semanticRoleLabel);
                }
            } else {
                semanticRoleLabelsSub.add(semanticRoleLabel);
            }
        }
        if (!isPrepositional) { // only numbered arguments need to be sorted.
            semanticRoleLabelsSub.sort((sl1, sl2) -> Integer.parseInt(sl1.toString().substring(3, 4))
                    - Integer.parseInt(sl2.toString().substring(3, 4)));
        }
        return semanticRoleLabelsSub;

    }

    // extract subject from A0
    public static Subject getSubject(SemanticRoleList semanticRole, SentenceParse sentenceAnalysis) {

        List<SemanticRoleLabel> semanticRoleLabels = getSortedSemanticList(semanticRole, false);
        if (semanticRoleLabels.isEmpty()) {
            return null;
        }
        //TODO: move this to a different place.
        String subject = semanticRole.get(semanticRoleLabels.get(0)).get().form();
        List<DependencyNode> subjectTokens = semanticRole.get(semanticRoleLabels.get(0)).get().tokens();
        if (subject.trim().equalsIgnoreCase("me")) {
            subject = "I";
        }
        return new Subject(subject.trim(), QuestionGeneratorHelper.getNamedEntitysubject(subject, sentenceAnalysis), subjectTokens);
    }

    /**
     * Get the object of the current srl text.
     * TODO: the dependency and the SRL tokens need to be centralized (else it will give a wrong output for a sentence with 2 POBJs of the same preposition).
     * TODO: also there is a difference of the SRL outputs in the chat and in the demo. (Needs to be upgraded!)
     */
    // TODO: Convert String to Object class for future compatibility
    //TODO: return object here.
    public static Optional<String> getObjectPhrase(SemanticRoleList semanticRole, String answerNode, boolean isPrepositional) {
        List<SemanticRoleLabel> semanticRoleLabels = getSortedSemanticList(semanticRole, isPrepositional);
        if (semanticRoleLabels.size() <= 1) {
            return Optional.empty();
        }
        String object = semanticRole.get(semanticRoleLabels.get(1)).get().form();
        if (answerNode != null) {
            if (object.contains(answerNode)) { // A1 is the object if it contains the answerNode
                return Optional.of(object);
            } else if ((semanticRoleLabels.size() > 2) && (semanticRole.get(semanticRoleLabels.get(2)).get().form().contains(answerNode))) {
                return Optional.of(semanticRole.get(semanticRoleLabels.get(2)).get().form()); // else A2 is the object
            } else {
                // check all AMs here
                if (isPrepositional) {
                    Optional<String> stringOptional = semanticRole.spans().stream()
                            .filter(s -> s.form().contains(answerNode))
                            .map(s -> s.form())
                            .findFirst();
                    return stringOptional;
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<String> extractObject(SemanticRoleList semanticRole) {
        List<SemanticRoleLabel> semanticRoleLabels = getSortedSemanticList(semanticRole, false);
        if (semanticRoleLabels.size() <= 1) {
            return Optional.empty();
        }
        return Optional.of(semanticRole.get(semanticRoleLabels.get(1)).get().form()); // else A2 is the object
    }

    static String getExtraFields(SemanticRoleList semanticRole, List<SemanticRoleLabel> excludeList, String object) {

        List<SemanticRoleLabel> semanticRoleLabels = getSortedSemanticList(semanticRole, false);
        List<SemanticRoleLabel> semanticRoleLabelList = new ArrayList<>(semanticRole.keySet());
        semanticRoleLabelList = putNumberedArgumentsFirst(semanticRoleLabelList);
        StringBuilder extraField = new StringBuilder();
        for (SemanticRoleLabel semanticRoleLabel : semanticRoleLabelList) { // TODO: ARG1 seems to be appearing at the end.
            // don't include the subject or the verb or (any special exclusion)
            if (semanticRoleLabel == SemanticRoleLabel.V || semanticRoleLabel.equals(semanticRoleLabels.get(0))
                    || excludeList.contains(semanticRoleLabel)) {
                continue;
            }
            // include all SRL arguments except for the argument which has the answer-node
            if (SEMANTIC_ROLE_LABEL_LIST.contains(semanticRoleLabel)
                    || semanticRoleLabel == SemanticRoleLabel.ARG1 || semanticRoleLabel == SemanticRoleLabel.ARG2 ||
                    semanticRoleLabel == SemanticRoleLabel.ARGM_ADV) {
                if (semanticRole.get(semanticRoleLabel).get().form().equals(object)) {
                    continue;
                }
                String toAppend = semanticRole.get(semanticRoleLabel).get().form().trim();
                if (semanticRoleLabel.isModifier()) {
                    toAppend = firstLetterShouldntBeCapital(toAppend);
                }
                extraField.append(toAppend);
                extraField.append(" ");
            }
        }
        return extraField.toString();
    }

    private static List<SemanticRoleLabel> putNumberedArgumentsFirst(List<SemanticRoleLabel> semanticRoleLabelList) {
        List<SemanticRoleLabel> numberedArguments = semanticRoleLabelList.stream().filter(srl -> srl.isNumberedArgument()).sorted().collect(Collectors.toList());
        // TODO: Note that the below sorting might disturb the order in which the AM arguments appear in the sentence.
        List<SemanticRoleLabel> modifierArguments = semanticRoleLabelList.stream().filter(srl -> !srl.isNumberedArgument()).sorted().collect(Collectors.toList());
        numberedArguments.addAll(modifierArguments);
        return numberedArguments;
    }

    /**
     * Assuming most of the extra fields would start with prepositions (and not Named Entities ).
     **/
    private static String firstLetterShouldntBeCapital(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private static Verb getVerbHelper(Verb verb) {

        if (verb.mainAuxWord() == null) {
            return verb;
        }

        String auxWord = verb.mainAuxWord().trim();
        String lemmaWord = verb.lemma().trim();
        if (auxWord.equals(lemmaWord) && auxWord.toLowerCase().equals("has")) {
            verb.lemma("have");
            verb.boolQuesVerb(verb.boolQuesVerb.split(",")[0] + "," + "have");
            verb.mainAuxWord("does");
            verb.verb("has");
        } else if (auxWord.equals(lemmaWord) && auxWord.toLowerCase().equals("have")) {
            verb.lemma("have");
            verb.mainAuxWord("do");
            verb.boolQuesVerb(verb.boolQuesVerb.split(",")[0] + "," + "have");
            verb.verb("has");
        } else if (auxWord.equalsIgnoreCase(lemmaWord) && auxWord.toLowerCase().equalsIgnoreCase("is")) {
            verb.lemma("");
            verb.mainAuxWord("is");
            verb.boolQuesVerb(verb.boolQuesVerb.split(",")[0] + "," + "is");
            verb.verb("is");
        }
        return verb;
    }

    static Verb getVerb(SemanticRoleList sematicRole) {
        DependencyNode verbNode = sematicRole.verb();
        StringBuilder sb = new StringBuilder();
        String mainAuxilliary = "";
        String secondaryAuxilliary = "";
        StringBuilder fullVerb = new StringBuilder();
        StringBuilder boolQuesVerb = new StringBuilder();
        PosLabel verbPos;
        // all modal and auxillaries are added
        for (DependencyNode children : verbNode.children()) {

            if (children.pos().equals(PosLabel.MD) || auxVebsList.contains(children.form())) {

                if (mainAuxilliary.equalsIgnoreCase("")) {
                    mainAuxilliary = children.form();
                } else {
                    secondaryAuxilliary = secondaryAuxilliary + " " + children.form();
                }
                if (children.form().equalsIgnoreCase("am")) {
                    fullVerb.append("is");
                } else {
                    fullVerb.append(children.form());
                }
                fullVerb.append(" ");
            }
        }

        verbPos = verbNode.pos();

        if (verbNode.depLabel().equals(DependencyLabel.COP)) {
            mainAuxilliary = verbNode.form();
            if (verbNode.form().equalsIgnoreCase("am")) {
                fullVerb.append("is");
            } else {
                fullVerb.append(verbNode.form());
            }
            fullVerb.append(" ");
        }

        if (sematicRole.get(SemanticRoleLabel.ARGM_NEG).isPresent()) {
            sb.append(sematicRole.get(SemanticRoleLabel.ARGM_NEG).get().form());
            sb.append(" ");
            fullVerb.append(sematicRole.get(SemanticRoleLabel.ARGM_NEG).get().form());
            fullVerb.append(" ");
            boolQuesVerb.append("No,");

        } else {
            boolQuesVerb.append("Yes,");
        }

        if (!verbNode.depLabel().equals(DependencyLabel.COP)) {

            fullVerb.append(verbNode.form());
        }

        if (mainAuxilliary.isEmpty()) {
            sb.append(verbNode.lemma());
            boolQuesVerb.append(verbNode.lemma());
            if (verbPos.equals(PosLabel.VBG)) {
                fullVerb.insert(0, "is ");
            }
            if (verbPos.equals(PosLabel.VB)) {
                fullVerb.append("s");
            }
            String doDoes = auxMap.getOrDefault(verbPos, "do");
            if (verbNode.head() != null && verbNode.head().pos().isVerb()) {
                doDoes = auxMap.getOrDefault(verbNode.head().pos(), "do");
            }
            Verb verb = new Verb(sb.toString().trim(), doDoes, "", fullVerb.toString(), boolQuesVerb.toString());
            return getVerbHelper(verb);
        } else {
            sb.append(verbNode.form());
            boolQuesVerb.append(verbNode.form());
            Verb verb = new Verb(sb.toString().trim(), mainAuxilliary, secondaryAuxilliary, fullVerb.toString(), boolQuesVerb.toString());
            return getVerbHelper(verb);
        }
    }

}