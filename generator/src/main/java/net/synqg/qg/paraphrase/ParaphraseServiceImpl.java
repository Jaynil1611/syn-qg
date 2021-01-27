package net.synqg.qg.paraphrase;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.var;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Paraphrasing service which queries the public PPDB API.
 *
 * @author kaustubhdholé.
 */
public class ParaphraseServiceImpl implements ParaphraseService {

    @Override
    public ParaphraseOutput generate(String query, List<String> pos) {
        Optional<PPDBResponse> ppdbResponse = generate(query, pos, 0);
        if (ppdbResponse.isPresent()) {
            PPDBResponse response = ppdbResponse.get();
            List<Paraphrase> paraphrases = response.hits().hit();
            int found = response.hits().found();
            for (int batch = 1; batch <= found / 20; batch++) {
                generate(query, pos, batch).ifPresent(x -> paraphrases.addAll(x.hits().hit()));

            }
            List<Paraphrase> filtered = filterLowPrecise(paraphrases);
            return new ParaphraseOutput(filtered, query, pos);
        }
        return new ParaphraseOutput(new ArrayList<>(), query, pos);
    }

    private List<Paraphrase> filterLowPrecise(List<Paraphrase> paraphrases) {
        return paraphrases.stream()
                .filter(p -> PPDBEntailment.isLowPrecise(p.entailment()))
                .collect(Collectors.toList());
    }

    private Optional<PPDBResponse> generate(String query, List<String> pos, int batch) {
        try {
            var params = new HashMap<>();
            params.put("q", query);
            if (!pos.isEmpty()) {
                String filters = pos.stream().map(p -> "[" + p + "]").collect(Collectors.joining(","));
                params.put("filter", filters);
            }
            params.put("lang", "en");
            params.put("batchNumber", String.valueOf(batch));
            var mapJoiner = Joiner.on("&").withKeyValueSeparator("=").join(params);
            URL url = new URL("http://paraphrase.org/api/en/search/?" + mapJoiner);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            if (con.getResponseCode() == 200) {
                @Cleanup BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String response = in.lines().collect(Collectors.joining());
                return Optional.of(new Gson().fromJson(response, PPDBResponse.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    @Override
    public ParaphraseOutput generate(String query) {
        return generate(query, new ArrayList<>());
    }

    @AllArgsConstructor
    public enum PosFilter {

        NN("NN"),//Noun, singular or mass
        NNS("NNS"),//Noun, plural
        NNP("NNP"),// Proper noun, singular
        NP("NP"),
        VB("VB"),// Verb, base form
        VBG("VBG"),// Verb, gerund or present participle
        VP("VP"),//Verb phrase
        VBN("VBN"),//Verb, past participle
        VBD("VBD"),//Verb, past tense
        S("S"),// Sentence
        JJ("JJ"),// Adjective
        IN("IN"),
        CD("CD"),//Cardinal number
        RB("RB"),//Adverb
        ADVP("ADVP"),//Adverb phrase
        ADJP("ADJP"),// Adjective phrase
        UH("UH"),// Interjection
        DT("DT"),//Determiner
        CC("CC");//Coordinating conjunction

        private String pos;

    }

    public static void main(String[] args) {
        ParaphraseService paraphraseService = new ParaphraseServiceImpl();
        List<String> pos = new ArrayList<>();
        pos.add("CC");
        pos.add("JJ");
        ParaphraseOutput paraphraseOutput = paraphraseService.generate("jump");
        System.out.println(paraphraseOutput.toString());
    }
}
