package net.synqg.qg.nlp.labels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Semantic Role Labels.
 * <p>
 * {@see https://raw.githubusercontent.com/clir/clearnlp-guidelines/master/md/specifications/semantic_role_labels.md}
 */
@Slf4j
@AllArgsConstructor
public enum SemanticRoleLabel {

    // Numbered Arguments
    ARG0("Agent", "0"),
    ARG1("Patient", "1"),
    ARG2("Instrument, benefactive, attribute", "2"),
    ARG3("Starting point", "3"),
    ARG4("Ending point", "4"),
    ARG5("Extra", "5"),
    ARGA("External causer", "a"),

    // Modifiers (these are prefixed by AM-)
    ARGM_ADJ("Adjectival (for nominal predicates)", "adj"),
    ARGM_ADV("Adverbial", "adv"),
    ARGM_CAU("Cause", "cau"),
    ARGM_COM("Comitative", "com"),
    ARGM_DIR("Direction", "dir"),
    ARGM_DIS("Discourse", "dis"),
    ARGM_DSP("Direct speech", "dsp"),
    ARGM_EXT("Extent", "ext"),
    ARGM_GOL("Goal", "gol"),
    ARGM_LOC("Location", "loc"),
    ARGM_LVB("Light verb", "lvb"),
    ARGM_MNR("Manner", "mnr"),
    ARGM_MOD("Modal", "mod"),
    ARGM_NEG("Negation", "neg"),
    ARGM_PNC("Purpose, not cause", "pnc"),
    ARGM_PRD("Secondary predication", "prd"),
    ARGM_PRP("Purpose", "prp"),
    ARGM_PRR("Light verb predicate", "prr"),
    ARGM_REC("Reciprocal", "rec"),
    ARGM_TMP("Temporal", "tmp"),

    V("Verb", "v"),
    UNKNOWN("Unknown", "unk");

    public static String STR_MODIFIER_PREFIX = "ARGM-";
    public static String ENUM_MODIFIER_PREFIX = "ARGM_";

    @Getter
    private String description;

    @Getter
    private String id;

    public boolean isNumberedArgument() {
        return this.id.matches("\\d");

    }

    public boolean isVerbOrModifier() {
        return isModifier() || this.equals(V);
    }

    public boolean isModifier() {
        return super.toString().startsWith(ENUM_MODIFIER_PREFIX);
    }

    @Override
    public String toString() {
        if (isModifier()) {
            return STR_MODIFIER_PREFIX + super.toString().substring(5);
        }
        return super.toString();
    }

    public static SemanticRoleLabel fromString(String label) {
        try {
            label = label.replace("-", "_");
            return SemanticRoleLabel.valueOf(label);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
