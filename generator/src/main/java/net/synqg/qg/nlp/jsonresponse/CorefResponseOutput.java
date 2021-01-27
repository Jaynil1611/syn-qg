package net.synqg.qg.nlp.jsonresponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class CorefResponseOutput {

    @SerializedName("outputs")
    private Outputs outputs;

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Outputs {

        @SerializedName("clusters")
        private List<List<List<Integer>>> clusters;

        @SerializedName("document")
        private List<String> document;
    }

}