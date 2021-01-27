package net.synqg.qg.nlp.jsonresponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class NERResponse {

    @SerializedName("tags")
    private List<String> tags;

    @SerializedName("words")
    private List<String> words;

}