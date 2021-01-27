package net.synqg.qg.ngram;

import com.google.common.base.Joiner;
import lombok.Cleanup;
import lombok.var;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class GoogleNgramService implements NgramService {

    final static String regex = "var data = \\[(\\{.*\\})\\];";

    public Optional<NgramOutput> fetchNgram(String searchTerm) {
        // make 2 attempts
        Optional<NgramOutput> ngramOutput = fetchNgram(searchTerm, 1900, 2000, 3, 15, false);
        if (ngramOutput.isPresent()) {
            return ngramOutput;
        } else {
            return fetchNgram(searchTerm, 1900, 2000, 3, 15, true);
        }
    }

    public Optional<NgramOutput> fetchNgram(String searchTerm, int startYear, int endYear, int smoothing, int corpus, boolean printErrorLogs) {
        try {
            var params = new HashMap<>();
            params.put("content", searchTerm.replace(" ", "%20"));
            params.put("year_start", String.valueOf(startYear));
            params.put("year_end", String.valueOf(endYear));
            params.put("corpus", String.valueOf(corpus));
            params.put("smoothing", String.valueOf(smoothing));
            var mapJoiner = Joiner.on("&").withKeyValueSeparator("=").join(params);
            var url = new URL("https://books.google.com/ngrams/graph?" + mapJoiner);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();// put a check to ensure that response code is 200
            @Cleanup InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
            @Cleanup BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            var pattern = Pattern.compile(regex);
            var matcher = pattern.matcher(response.toString());
            if (matcher.find()) {
                JSONObject json = (JSONObject) JSONValue.parse(matcher.group(1));
                return Optional.of(parse(json));
            }
            con.disconnect();
        } catch (Exception e) {
            if (printErrorLogs) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    private NgramOutput parse(JSONObject json) {
        JSONArray jsonArray = (JSONArray) json.get("timeseries");
        List<Double> timeseries = new ArrayList<>();
        jsonArray.stream().forEach(x -> timeseries.add((Double) x));
        return new NgramOutput((String) json.get("parent"), timeseries, (String) json.get("ngram"), (String) json.get("type"));
    }


    public Double centuryAverage(String words) {
        Optional<NgramOutput> ngramOutput = fetchNgram(words);
        if (ngramOutput.isPresent()) {
            NgramOutput ngramOutput1 = ngramOutput.get();
            Double centuryAverage = ngramOutput1.timeseries()
                    .stream()
                    .mapToDouble(d -> d)
                    .average().orElse(0D);
            return centuryAverage;
        }
        return 0D;
    }

}
