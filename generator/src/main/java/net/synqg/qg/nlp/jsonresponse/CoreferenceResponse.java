package net.synqg.qg.nlp.jsonresponse;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * * AllenNLP coreference.
 * *
 * * @author kaustubhdholé.
 */
@Getter
@Setter
@Accessors(fluent = true)
public class CoreferenceResponse {

    private List<List<List<Integer>>> clusters;

    private List<String> document;

    @SerializedName("predicted_antecedents")
    private List<Integer> predictedAntencedents;

    @SerializedName("top_spans")
    private List<List<Integer>> topSpans;

}
