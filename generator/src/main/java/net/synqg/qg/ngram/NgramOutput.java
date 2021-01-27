package net.synqg.qg.ngram;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@Accessors(fluent = true)
public class NgramOutput {
    String parent;
    List<Double> timeseries;
    String ngram;
    String type;
}
