package net.synqg.qg.implicative;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Types of implications used by verbs as well as phrases (verb-noun collocations)
 *
 * @author kaustubhdholé.
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum ImplicativeType {

    /**
     * cause NP to, force NP to, make NP to
     * Kim forced Mary to leave. --> Kim left.
     */
    POS_POS(Boolean.TRUE, Boolean.TRUE),
    /**
     * refuse to, prevent NP from, keep NP from
     * Kim prevented Mary from leaving. --> Kim did not leave.
     */
    POS_NEG(Boolean.TRUE, Boolean.FALSE),
    /**
     * hesitate to
     * Kim did not hesitate to speak her mind. --> Kim spoke her mind.
     */
    NEG_POS(Boolean.FALSE, Boolean.TRUE),
    /**
     * can (= be able to)
     * Kim could not finish her sentence.
     */
    NEG_NEG(Boolean.FALSE, Boolean.FALSE);


    /**
     * Polarity of the host clause.
     */
    private Boolean mainClausePolarity;
    /**
     * Polarity of the subordinate clause.
     */
    private Boolean entailedPolarity;


    public Boolean match(Boolean mainClausePolarity) {
        return this.mainClausePolarity == mainClausePolarity;
    }

    public static ImplicativeType fromString(String type) throws Exception {
        type = type.replace("*", "");
        switch (type) {
            case "np":
                return NEG_POS;
            case "nn":
                return NEG_NEG;
            case "pn":
                return POS_NEG;
            case "pp":
                return POS_POS;
            default:
                throw new Exception("Unsupported type: " + type);
        }
    }

}
