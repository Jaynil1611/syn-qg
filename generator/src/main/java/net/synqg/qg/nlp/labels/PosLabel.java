package net.synqg.qg.nlp.labels;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum PosLabel {

    CC("Coordinating conjunction"),
    CD("Cardinal number"),
    DT("Determiner"),
    EX("Existential there"),
    FW("Foreign word"),
    IN("Preposition or subordinating conjunction"),
    JJ("Adjective"),
    JJR("Adjective, comparative"),
    JJS("Adjective, superlative"),
    LS("List item marker"),
    MD("Modal"),
    NN("Noun, singular or mass"),
    NNS("Noun, plural"),
    NNP("Proper noun, singular"),
    NNPS("Proper noun, plural"),
    PDT("Predeterminer"),
    POS("Possessive ending"),
    PRP("Personal pronoun"),
    PRP$("Possessive pronoun"),
    RB("Adverb"),
    RBR("Adverb, comparative"),
    RBS("Adverb, superlative"),
    RP("Particle"),
    SYM("Symbol"),
    TO("to"),
    UH("Interjection"),
    VB("Verb, base form"),
    VBD("Verb, past tense"),
    VBG("Verb, gerund or present participle"),
    VBN("Verb, past participle"),
    VBP("Verb, non-3rd person singular present"),
    VBZ("Verb, 3rd person singular present"),
    WDT("Wh-determiner"),
    WP("Wh-pronoun"),
    WP$("Possessive wh-pronoun"),
    WRB("Wh-adverb"),
    PUNC_CLOSER("Punctuation mark, sentence closer"),
    PUNC_COMMA("Punctuation mark, comma"),
    PUNC_COLON("Punctuation mark, colon"),
    CTX_LP("Contextual separator, left paren"),
    CTX_RP("Contextual separator, right paren"),
    UNKNOWN("Holder for unknown tag"),
    LRB("-LRB-"),
    RRB("-RRB-"),
    PUNC_LQUOTE("``"),
    PUNC_RQUOTE("''");

    public static final ImmutableSet ADJECTIVES;
    private static final Set<String> ALL_TAGS;

    static {
        ADJECTIVES = Sets.immutableEnumSet(PosLabel.JJ, PosLabel.JJR, PosLabel.JJS);
        ALL_TAGS = Sets.newHashSet(Arrays.stream(PosLabel.values()).map(PosLabel::toString).collect(Collectors.toSet()));
    }


    private String description;


    PosLabel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOneOf(PosLabel pos, PosLabel... rest) {
        return EnumSet.of(pos, rest).contains(this);
    }


    public boolean isVerb() {
        return EnumSet.of(VB, VBD, VBG, VBN, VBZ, VBP).contains(this);
    }


    public boolean isWildCard() {
        return EnumSet.of(WP, WP$, WRB, WDT).contains(this);
    }


    public boolean isNoun() {
        return EnumSet.of(NN, NNP, NNPS, NNS).contains(this);
    }


    public boolean isPronoun() {
        return EnumSet.of(PRP, PRP$).contains(this);
    }


    public boolean isAdj() {
        return EnumSet.of(JJ, JJR, JJS).contains(this);
    }


    public boolean isPrep() {
        return EnumSet.of(IN, TO).contains(this);
    }


    public boolean isInterj() {
        return this == UH;
    }


    public boolean isDeterminer() {
        return this == DT;
    }


    public boolean isAdv() {
        return EnumSet.of(RB, RBR, RBS).contains(this);
    }


    public boolean isProperNoun() {
        return EnumSet.of(NNP, NNPS).contains(this);
    }


    public boolean isPunctuation() {
        return EnumSet.of(PUNC_CLOSER, PUNC_LQUOTE, PUNC_RQUOTE, PUNC_COMMA, PUNC_COLON).contains(this);
    }

    /**
     * Use instead of valueOf().
     *
     * <p>.;?* become PUNC_CLOSER.
     *
     * <p>, becomes PUNC_COMMA.
     *
     * <p>: becomes PUNC_COLON.
     *
     * <p>( becomes CTX_LP.
     *
     * <p>) becomes CTX_RP.
     *
     * <p>The rest are delegated to valueOf.  An unknown tag becomes UNKNOWN.
     */
    public static PosLabel fromString(String str) {
        switch (str) {
            case ".":
            case ";":
            case "?":
            case "*":
                return PUNC_CLOSER;
            case "``":
                return PUNC_LQUOTE;
            case "''":
            case "\\'\\'":
                return PUNC_RQUOTE;
            case ",":
                return PUNC_COMMA;
            case ":":
                return PUNC_COLON;
            case "(":
                return CTX_LP;
            case ")":
                return CTX_RP;
            default: {
                String tag = str.replaceAll("-", "");
                if (ALL_TAGS.contains(tag)) {
                    try {
                        return valueOf(tag);
                    } catch (Exception e) {
                        System.out.println("Received unknown Penn POS tag: " + str);
                    }
                }
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        switch (this) {
            case PUNC_LQUOTE:
                return "``";
            case PUNC_RQUOTE:
                return "\\'\\'";
            case PUNC_CLOSER:
                return ".";
            case PUNC_COMMA:
                return ",";
            case PUNC_COLON:
                return ":";
            case CTX_LP:
                return "(";
            case CTX_RP:
                return ")";
            case RRB:
                return "-RRB-";
            case LRB:
                return "-LRB-";
            default:
                return super.toString();
        }
    }
}
