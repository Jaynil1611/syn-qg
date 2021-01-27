package net.synqg.qg.service.processors.input;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import net.synqg.qg.service.DefaultQgAnalysisService;
import net.synqg.qg.nlp.jsonresponse.CoreferenceResponse;
import net.synqg.qg.nlp.jsonresponse.DependencyResponse;
import net.synqg.qg.service.processors.Preprocess;

public class CorefPreprocessor implements Preprocess {

    public String process(String message) {

        try {
            String output =  DefaultQgAnalysisService.getJson(message, "predictcoref");
            Gson gson = new Gson();
            CoreferenceResponse result = gson.fromJson(output, CoreferenceResponse.class);
            List<String> document = result.document();
            for (List<List<Integer>> cluster : result.clusters()) {

                String clusterRep = getclusterrep(cluster.get(0).get(0), cluster.get(0).get(1), result.document());
                String clusteroutput =  DefaultQgAnalysisService.getJson(clusterRep, "predictdep");
                DependencyResponse clusterResult = gson.fromJson(clusteroutput, DependencyResponse.class);

                if (!clusterResult.posLabels().contains("NNP")) {
                    continue;
                }

                for (List<Integer> clusterElement: cluster.subList(1, cluster.size())) {

                    int start = clusterElement.get(0);
                    int end = clusterElement.get(1);
                    List<String> subList = new ArrayList<String>(result.document().subList(start, end+1));
                    document.removeAll(subList);
                    document.add(start, clusterRep);

                }
            }
            return String.join(" ", document);

        } catch (Exception e) {
            return message;
        }



    }

    private String getclusterrep(Integer integer, Integer integer1, List<String> document) {

        StringBuilder sb = new StringBuilder();

        for (int start = integer; start <=integer1; start++) {
            sb.append(document.get(start));
            sb.append(" ");
        }
        return sb.toString();

    }

}
