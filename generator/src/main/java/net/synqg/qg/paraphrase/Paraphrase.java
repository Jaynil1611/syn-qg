package net.synqg.qg.paraphrase;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * Single PPDB JSON response.
 *
 * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class Paraphrase {
    String source;
    String lhs;
    String part_of_speech;
    int score_up;
    int score_down;
    String default_formula_expr;
    String entailment;
    String firstappearsin;
    int id;
    String p_e_f;
    String p_e_flhs;
    String p_f_e;
    String p_f_elhs;
    String p_lhs_e;
    String p_lhs_f;
    String raritypenalty;
    String target;
}
