package net.synqg.qg.lm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Language Model Client which sends sentence and
 * gets LM loss (OpenAI GPT model)
 * for the sentence.
 *
 * @author kaustubhdholé.
 */
public class DefaultLanguageModelScoringService implements LanguageModelScoringService {

    private PrintWriter pw;
    private BufferedReader br;

    /**
     * Call the constructor every time the server is started & only after the server is full started.
     */
    public DefaultLanguageModelScoringService() {
        String serverName = "127.0.0.1";
        int port = 12358;
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public Float getLanguageModelLoss(String sentence) throws IOException {
        if (sentence == null || sentence.isEmpty()) {
            System.out.println("Found a null or a blank string.");
            return 10f;
        }
        pw.println(sentence);
        String received = br.readLine();
        Float value = Float.valueOf(received);
        return value;
    }

}
