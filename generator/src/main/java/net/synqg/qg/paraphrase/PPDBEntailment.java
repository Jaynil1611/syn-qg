package net.synqg.qg.paraphrase;

import java.util.EnumSet;

/**
 * Different types of entailment relations appearing in PPDB.
 *
 * @author kaustubhdholé.
 */
public enum PPDBEntailment {

    Equivalence,// look at/watch
    ForwardEntailment,
    ReverseEntailment,
    Independent,
    OtherRelated;

    public static boolean isEquivalent(String entailment) {
        return entailment.equalsIgnoreCase(Equivalence.name());
    }

    public static boolean isLowPrecise(String entailment) {
        return EnumSet.of(ReverseEntailment, OtherRelated, Equivalence).contains(PPDBEntailment.valueOf(entailment));
    }
}

