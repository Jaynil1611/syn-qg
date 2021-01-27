package net.synqg.qg.implicative;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;

/**
 * The category of implication (one-way or two-way) for both simple and phrasal implicatives.
 *
 * @author kaustubhdholé.
 */
@Setter
@Getter
@Accessors(fluent = true)
@NoArgsConstructor
public class Category {

    private String syntacticCategory;
    private Optional<ImplicativeType> implicationType = Optional.empty();
    private Optional<TwoWayImplicativeType> twoWayImplicativeType = Optional.empty();
    private String example;

    /**
     * Constructor for defining a category from the simple/two-way implication format.
     *
     * @param simplePhrasalFormat "impl_pn_np"
     */
    public Category(String simplePhrasalFormat) throws Exception {
        String[] splits = simplePhrasalFormat.split("\t");
        syntacticCategory = splits[1];
        String type = splits[2].replace("impl_", "");
        if (type.contains("_")) {
            twoWayImplicativeType = Optional.of(TwoWayImplicativeType.fromString(type, "_"));
        } else {
            implicationType = Optional.of(ImplicativeType.fromString(type));
        }
        if (splits.length > 3) {
            example = splits[3];
        }
    }

    /**
     * Format in the phrasal-implicatives.txt format.
     * pp|nn format.
     *
     * @param implicationSignature "pp|nn" or "pn|np" or "pn", etc
     * @return Category(POS_POS, NEG_NEG)
     */
    public static Category fromPhrasalImplicativeFormat(String implicationSignature) throws Exception {
        Category category = new Category();
        if (implicationSignature.contains("|")) {
            category.twoWayImplicativeType(Optional.of(TwoWayImplicativeType.fromString(implicationSignature, "\\|")));
        } else {
            category.implicationType(Optional.of(ImplicativeType.fromString(implicationSignature)));
        }
        return category;
    }

    /**
     * The function which computes the entailed polarity from the main clause polarity.
     * @param mainClausePolarity true/false
     * @return "Happened" or "Didn't happen" or "Unsure"
     */
    public Optional<EntailedPolarity> toEntailedPolarity(boolean mainClausePolarity) {
        if (this.implicationType().isPresent()) {
            ImplicativeType type = this.implicationType().get();
            if (type.match(mainClausePolarity)) {
                return Optional.of(EntailedPolarity.fromBoolean(type.entailedPolarity()));
            } else {
                // this is the case for one-way implications in which we don't know if the fact has happened or not.
                // By definition, the entailed factual correctness/veridicality is not proven.
                return Optional.of(EntailedPolarity.UNSURE);
            }
        }
        if (this.twoWayImplicativeType().isPresent()) {
            TwoWayImplicativeType type = this.twoWayImplicativeType().get();
            return Optional.of(EntailedPolarity.fromBoolean(type.getEntailedPolarity(mainClausePolarity)));
        }
        return Optional.empty();
    }
}