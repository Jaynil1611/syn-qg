package net.synqg.qg.nlp.labels;


import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Universal dependencies and Stanford Typed dependencies.
 */
@Slf4j
public enum DependencyLabel {

    // UD V2 http://universaldependencies.org/u/dep/index.html
    ACL("clausal modifier of noun (adjectival clause)"),
    ACLRELCL("relative clause modifier", ACL),
    ADVCL("adverbial clause modifier"),
    ADVMOD("adverbial modifier"),
    AMOD("adjectival modifier"),
    APPOS("appositional modifier"),
    AUX("auxiliary"),
    CASE("case marking"),
    CC("coordinating conjunction"),
    CCPRECONJ("preconjunct", CC),
    CCOMP("clausal complement"),
    CLF("classifier"),
    COMPOUND("compound"),
    COMPOUNDPRT("phrasal verb particle", COMPOUND),
    CONJ("conjunct"),
    COP("copula"),
    CSUBJ("clausal subject"),
    DEP("unspecified net.ipsoft.qg.nlg.nlp"),
    DET("determiner"),
    DETPREDET("predeterminer", DET),
    DISCOURSE("discourse element"),
    DISLOCATED("dislocated elements"),
    EXPL("expletive"),
    FIXED("fixed multiword expression"),
    FLAT("flat multiword expression"),
    GOESWITH("goes with"),
    IOBJ("indirect object"),
    LIST("list"),
    MARK("marker"),
    NMOD("nominal modifier"),
    NMODNPMOD("noun phrase as adverbial modifier", NMOD),
    NMODPOSS("possessive nominal modifier", NMOD),
    NMODTMOD("temporal modifier", NMOD),
    NSUBJ("nominal subject"),
    NUMMOD("numeric modifier"),
    OBJ("object"),
    OBL("oblique nominal"),
    OBLTMOD("oblique nominal temporal mod", OBL),
    ORPHAN("orphan"),
    PARATAXIS("parataxis"),
    PUNCT("punctuation"),
    REPARANDUM("overridden disfluency"),
    ROOT("root"),
    VOCATIVE("vocative"),
    XCOMP("open clausal complement"),

    // Stanford typed dependencies extracted from
    // http://nlp.stanford.edu/software/dependencies_manual.pdf (revised April, 2015)
    // Also added in UD. http://universaldependencies.org/u/dep/index.html
    ACOMP("adjectival complement"),
    AGENT("agent"),
    AUXPASS("passive auxiliary"),
    CSUBJPASS("clausal passive subject"),
    DOBJ("direct object"),
    MWE("multi-word expression"),
    NEG("negation modifier"),
    NN("noun compound modifier"),
    NPADVMOD("noun phrase as adverbial modifier"),
    NSUBJPASS("passive nominal subject"),
    NUM("numeric modifier"),
    NUMBER("element of compound number"),
    PCOMP("prepositional complement"),
    POBJ("object of a preposition"),
    POSS("possession modifier"),
    POSSESSIVE("possessive modifier"),
    PRECONJ("preconjunct"),
    PREDET("predeterminer"),
    PREP("prepositional modifier"),
    PREPC("prepositional clausal modifier"),
    PRT("phrasal verb particle"),
    QUANTMOD("quantifier phrase modifier"),
    RCMOD("relative clause modifier"),
    REF("referent"),
    TMOD("temporal modifier"),
    VMOD("reduced non-finite verbal modifier"),
    XSUBJ("controlling subject"),

    // Used if we receive an unknown label
    UNKNOWN("unknown label");

    public final String description;

    @Getter
    public final DependencyLabel baseLabel;

    DependencyLabel(String description) {
        this.description = description;
        this.baseLabel = this;
    }

    DependencyLabel(String description, DependencyLabel baseLabel) {
        this.description = description;
        this.baseLabel = baseLabel;
    }

    /**
     * Get DependencyLabel from string.
     *
     * @param lcString a deplabel string.
     * @return a DependencyLabel
     */
    public static DependencyLabel fromString(@NonNull String lcString) {
        DependencyLabel ret = tryParse(lcString);
        if (null == ret) {
            log.trace("Received unknown DependencyLabel {}", lcString);
            return UNKNOWN;
        }
        return ret;
    }

    public static DependencyLabel tryParse(@NonNull String lcString) {
        try {
            lcString = lcString.replace(":", "");
            return DependencyLabel.valueOf(lcString.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isOneOf(final DependencyLabel... dependencyLabels) {
        return Sets.newHashSet(dependencyLabels).contains(this);
    }

}
