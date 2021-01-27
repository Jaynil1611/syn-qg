package net.synqg.qg.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by kdhole on 4/8/16.
 */
@Slf4j
@Accessors(chain = true)
public abstract class FileOperationUtils {

    private char[] appendWithHash(String sentence, String randomId) {
        String newUtter = "";
        for (String token : sentence.split(" ")) {
            newUtter += (token + "#" + randomId + " ");
        }
        newUtter += (" <eos>#" + randomId + " ");
        return newUtter.toCharArray();
    }


    public static void deleteFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void writeFilesAppend(String path, String text) {
        BufferedWriter bwr = null;
        try {
            File file = new File(path);
            FileWriter fw = new FileWriter(file, true);
            bwr = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bwr);
            out.print(text);

        } catch (IOException ex) {
            log.info("Exception occurred while writing " ,ex);
        } finally {
            if (bwr != null) {
                try {
                    bwr.close();
                } catch (IOException e) {
                    log.info("Failed to close buffered writer", e);
                }
            }
        }
    }

    static public ArrayList<String> readLines(String uniqueResponsesPath) {
        ArrayList<String> uniqueAgentResponses = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(uniqueResponsesPath)))){
            String line;
            while ((line = br.readLine()) != null) {
                uniqueAgentResponses.add(line.trim());
            }
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return uniqueAgentResponses;
    }

    static public ArrayList<String> readAllFilesFromFolder(String uniqueResponsesPath) {

        ArrayList<String> uniqueAgentResponses = new ArrayList<String>();
        File folder = new File(uniqueResponsesPath);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                try (BufferedReader br = new BufferedReader(new FileReader(new File(uniqueResponsesPath)))){

                    String line;
                    while ((line = br.readLine()) != null) {
                        uniqueAgentResponses.add(line.trim());
                    }
                } catch (Exception ex) {
                }
            }
        }
        return uniqueAgentResponses;
    }

    public static void writeFilesAppend(String path, List<String> lines) {
        for(String text : lines){
            writeFilesAppend(path,text);
        }
    }

    public static void writeFiles(String path, String text) {
        BufferedWriter bwr = null;
        try {
            File file = new File(path);
            FileWriter fw = new FileWriter(file);
            bwr = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bwr);
            out.print(text);

        } catch (IOException ex) {
            log.info(ex.getMessage());
        } finally {
            if (bwr != null) {
                try {
                    bwr.close();
                } catch (IOException e) {
                    log.info("Failed to close buffered writer");
                }
            }
        }
    }

    public static FileWriter createNewFile(String path) {
        try {
            Files.deleteIfExists(new File(path).toPath());
            return new FileWriter(new File(path));
        } catch (Exception e) {
            return null;
        }
    }

    public static FileWriter createNewFile(File file) throws IOException {
        Files.deleteIfExists(file.toPath());
        return new FileWriter(new File(file.toPath().toString()));
    }

}

