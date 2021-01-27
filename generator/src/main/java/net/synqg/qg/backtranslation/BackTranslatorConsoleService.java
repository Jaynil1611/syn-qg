package net.synqg.qg.backtranslation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Service which translates an English sentence into German
 * and then back to English via the models trained
 * on WMT (fair-seq models with BPE).
 *
 * @author kaustubhdholé.
 */
public class BackTranslatorConsoleService {

    public static void main(String[] args) {
        {
            String serverName = "127.0.0.1";
            int port = 12389;
            try {
                System.out.println("Connecting to " + serverName + " on port " + port);
                Socket client = new Socket(serverName, port);

                System.out.println("Just connected to " + client.getRemoteSocketAddress());
                //DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));


                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
                System.out.println("Initiated reader and writer.");
                System.out.println();
                while (true) {
                    //Enter data using BufferReader
                    BufferedReader console =
                            new BufferedReader(new InputStreamReader(System.in));

                    // Reading data using readLine
                    String sentence = console.readLine();

                    pw.println(sentence);
                    String received = br.readLine();

                    System.out.println(received);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/* Python server code for BackTranslation
import socket
import torch

print("Torch version: " + torch.__version__)

#torch.hub.list('pytorch/fairseq')  # [..., 'transformer.wmt16.en-de', ... ]

print("Starting to load English to German Translation Model:")
en2de = torch.hub.load('pytorch/fairseq', 'transformer.wmt16.en-de', tokenizer='moses', bpe='subword_nmt' , force_reload=True)
print("Completed loading English to German Translation Model.\n")
print("Starting to load German to English Translation Model:")
de2en = torch.hub.load('pytorch/fairseq', 'transformer.wmt19.de-en.single_model', tokenizer='moses', bpe='fastbpe')
print("Completed loading German to English Translation Model.\n")

host = '127.0.0.1'        # Symbolic name meaning all available interfaces
port = 12369     # Arbitrary non-privileged port
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(1)
conn, addr = s.accept()
print('Connected by', addr)
while True:
    data = (conn.recv(1024))
    if not data: break
    print("Computing the German Translation for:")
    en = data.decode()
    print(en)
    de=en2de.translate(en)
    print(de)
    y = de2en.translate(de)
    print(y)
    conn.sendall((y + "\n").encode('utf8'))
conn.close()

 */