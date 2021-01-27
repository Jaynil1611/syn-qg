package net.synqg.qg.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods.
 *
 * @author kaustubhdholé.
 */
@UtilityClass
public class SynQgUtils {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public List<String> whQuestions() {
        List<String> whords = new ArrayList<>();
        whords.add("who");
        whords.add("which");
        whords.add("what");
        whords.add("why");
        whords.add("how");
        whords.add("when");
        whords.add("where");
        return whords;
    }

    List<String> thatPronouns() {
        return Arrays.asList("that", "those", "it", "this", "these", "which");
    }

    List<String> personalPronouns() {
        return Arrays.asList("i", "me", "myself",
                "we", "our", "ourself", "ourselves",
                "he", "she", "him", "her", "himself", "herself", "his",
                "they", "them", "their", "themselves");
    }
}
