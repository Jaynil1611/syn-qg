package net.synqg.qg.backtranslation;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

/**
 * Service which translates an English sentence into German
 * and then back to English.
 *
 * @author kaustubhdholé.
 */
@Slf4j
public class DefaultBackTranslationService implements BackTranslationService {

    private PrintWriter pw;
    private BufferedReader br;
    private Map<String, String> cache;

    public DefaultBackTranslationService(Map<String, String> cache) {
        this.cache = cache;
        System.out.println("Load a cache of " + cache.size() + " back-translations");
        String serverName = "127.0.0.1";
        int port = 12389;
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw = new PrintWriter(client.getOutputStream(), true);
            System.out.println("Initiated reader and writer.");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public String backTranslate(String sentence) throws Exception {
        if (sentence == null || sentence.isEmpty()) {
            System.out.println("Found a null or a blank string.");
            return "";
        }
        if (cache.containsKey(sentence)) {
            System.out.println("Extract from cache.");
            return cache.get(sentence);
            //
        }

        pw.println(sentence);
        String received = br.readLine();
        if (received.contains("< unk >") || received.contains("<unk>")) {
            log.warn("<UNK> token found: " + received + "Returning original sentence");
            return sentence;
        }
        if (received.equalsIgnoreCase("UnicodeDecodeError")) {
            return "UnicodeDecodeError";
        }
        //cache.put(sentence, received);
        return received;
    }


}
