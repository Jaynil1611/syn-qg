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
public class LanguageModelConsoleScorer {

    public static void main(String[] args) {
        String serverName = "127.0.0.1";
        int port = 12358;
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            //DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));


            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter pw = new PrintWriter(client.getOutputStream(), true);

            System.out.println();
            while (true) {
                //Enter data using BufferReader
                BufferedReader console =
                        new BufferedReader(new InputStreamReader(System.in));

                // Reading data using readLine
                String sentence = console.readLine();

                pw.println(sentence);
                String received = br.readLine();

                Float value = Float.valueOf(received);
                System.out.println(received);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Python code for Language model Server.
/*
import socket
import torch
import math

from pytorch_pretrained_bert import OpenAIGPTTokenizer, OpenAIGPTModel, OpenAIGPTLMHeadModel
print("Loading pre-trained model (weights)")
model = OpenAIGPTLMHeadModel.from_pretrained('openai-gpt')
model.eval()
print("Loading pre-trained model tokenizer (vocabulary)")
tokenizer = OpenAIGPTTokenizer.from_pretrained('openai-gpt')
def score(sentence):
    tokenize_input = tokenizer.tokenize(sentence)
    tensor_input = torch.tensor([tokenizer.convert_tokens_to_ids(tokenize_input)])
    loss=model(tensor_input, lm_labels=tensor_input)
    return math.exp(loss/(len(tokenize_input)))


host = '127.0.0.1'        # Symbolic name meaning all available interfaces
port = 12358     # Arbitrary non-privileged port
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(1)
conn, addr = s.accept()
print('Connected by', addr)
while True:
    data = (conn.recv(1024))
    if not data: break
    print("Computing the loss for the sentence: ")
    print(data.decode())
    p=score(data.decode())
    print(str(p))

    #data = int(data) + 1
    conn.sendall((str(p) + "\n").encode('utf8'))
conn.close()
 */
