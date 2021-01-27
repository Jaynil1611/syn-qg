package net.synqg.qg.implicative;

/**
 * The polarity entailed from the main clause.
 *
 * @author kaustubhdholé.
 */
public enum EntailedPolarity {

    /**
     * Implies this event has for sure happened.
     */
    HAPPENED,

    /**
     * Implies this event did not happen.
     */
    DID_NOT_HAPPEN,

    /**
     * We have no information whether this event has happened or not.
     */
    UNSURE;

    EntailedPolarity() {
    }

    public static EntailedPolarity fromBoolean(Boolean polarity) {
        if (polarity) {
            return HAPPENED;
        } else {
            return DID_NOT_HAPPEN;
        }
    }

    public boolean hasHappened() {
        return this.equals(HAPPENED);
    }
}
