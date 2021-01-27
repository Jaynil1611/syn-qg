package net.synqg.qg.ngram;

import java.util.Optional;

public interface NgramService {

    Optional<NgramOutput> fetchNgram(String searchTerm);

    Optional<NgramOutput> fetchNgram(String searchTerm, int startYear, int endYear, int smoothing, int corpus, boolean printErrorLogs);

    Double centuryAverage(String words);
}