package net.synqg.qg.implicative;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The four possible types of two way implications.
 *
 * @author kaustubhdholé.
 */
@Getter
@Accessors(fluent = true)
public enum TwoWayImplicativeType {

    /**
     * Constructions like "forget to X", "neglect to X", etc.
     * + - | - +
     */
    FORGET_TO_TYPE(ImplicativeType.POS_NEG, ImplicativeType.NEG_POS),

    /**
     * Constructions like "remember to X", "turn out that X", "manage to X"
     * or phrases like "took an effort to X"
     * + + | - - or Equi-construction.
     */
    EQUIVALENCE(ImplicativeType.POS_POS, ImplicativeType.NEG_NEG),

    /**
     * Constructions like "forget that X"
     */
    FACTIVE(ImplicativeType.POS_POS, ImplicativeType.NEG_POS),

    /**
     * Constructions like "pretend to X", "pretend that X".
     */
    COUNTER_FACTIVE(ImplicativeType.POS_NEG, ImplicativeType.NEG_NEG);

    private List<ImplicativeType> types;

    public Boolean getEntailedPolarity(Boolean mainClausePolarity) {
        if (types.get(0).match(mainClausePolarity)) {
            return types.get(0).entailedPolarity();
        } else {
            return types.get(1).entailedPolarity();
        }
    }

    TwoWayImplicativeType(ImplicativeType first, ImplicativeType second) {
        types = new ArrayList<>();
        types.add(first);
        types.add(second);
    }

    public static TwoWayImplicativeType fromString(String implicationSignature, String splitBy) throws Exception {
        if (implicationSignature.equalsIgnoreCase("fact_p")) {
            return FACTIVE;
        }
        String[] splits = implicationSignature.split(splitBy);
        ImplicativeType first = ImplicativeType.fromString(splits[0]);
        ImplicativeType second = ImplicativeType.fromString(splits[1]);
        return fromBothTypes(first, second);
    }

    private static TwoWayImplicativeType fromBothTypes(ImplicativeType first, ImplicativeType second) {
        Set<ImplicativeType> types = new HashSet<>();
        types.add(first);
        types.add(second);
        if (FORGET_TO_TYPE.types.containsAll(types)) {
            return FORGET_TO_TYPE;
        } else if (EQUIVALENCE.types.containsAll(types)) {
            return EQUIVALENCE;
        } else if (FACTIVE.types.containsAll(types)) {
            return FACTIVE;
        } else {
            return COUNTER_FACTIVE;
        }
    }
}
// how to detect extra-position ?