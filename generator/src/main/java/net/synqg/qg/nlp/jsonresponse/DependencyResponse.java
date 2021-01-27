package net.synqg.qg.nlp.jsonresponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class DependencyResponse {

    @SerializedName("predicted_heads")
    private List<Integer> nodeHeads;

    @SerializedName("predicted_dependencies")
    private List<String> depLabels;

    @SerializedName("pos")
    private List<String> posLabels;

    @SerializedName("words")
    private List<String> words;

}
