package net.synqg.qg.nlp.jsonresponse;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class SemanticResponse {

    @SerializedName("verbs")
    private Verb[] verbs;

    @SerializedName("words")
    private String[] words;

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class Verb {
        @SerializedName("description")
        private String description;
        @SerializedName("tags")
        private String[] tags;
        @SerializedName("verb")
        private String verb;
    }

}
