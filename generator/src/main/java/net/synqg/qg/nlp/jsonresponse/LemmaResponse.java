package net.synqg.qg.nlp.jsonresponse;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class LemmaResponse {

    @SerializedName("lemmas")
    private List<String> lemmas;

    @SerializedName("words")
    private List<String> words;

}