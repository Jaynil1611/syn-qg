package net.synqg.qg.paraphrase;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * PPDB JSON response.
 *
 * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class HitWrapper {

    int start;

    int found;

    List<Paraphrase> hit;
}
