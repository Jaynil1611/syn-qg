package net.synqg.qg.implicative;

import lombok.extern.slf4j.Slf4j;
import net.synqg.qg.utils.FileOperationUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * List of adjectives with their implication signatures.
 * (1) XLEADJ-COMP.txt contains 278 adjectives that take sentential that-complements such as "certain," "happy," "worried."
 * (2) XLEADJ-EXTRAfin.txt contains 695 adjectives that take extraposed that-complements such as "absurd,' "encouraging," and "wise."
 *
 * @author kaustubhdholé.
 */
@Slf4j
public class AdjectiveImplicativesResource {

    private static final String RESOURCES_PATH = "net/synqg/qg/implicative/";

    /**
     * 278 (250 after removing noimpl) adjectives that take sentential that-complements.
     */
    private Map<String, Category> adjectivesSentential = new HashMap<>();

    /**
     * 695 (649) adjectives that take extraposed that-complements.
     */
    private Map<String, Category> adjectivesExtraposed = new HashMap<>();

    public AdjectiveImplicativesResource() {
        ClassLoader classLoader = this
                .getClass()
                .getClassLoader();
        FileOperationUtils.readLines(classLoader
                .getResource(RESOURCES_PATH + "XLEADJ-COMP.txt").getPath())
                .forEach(l -> {
                    String[] splits = l.split("\t");
                    if (!splits[2].equalsIgnoreCase("noimpl")) {
                        try {
                            adjectivesSentential.put(splits[0], new Category(l));
                        } catch (Exception e) {
                            log.error("Unable to split line {} due to ", l, e);
                        }
                    }
                });
        FileOperationUtils.readLines(classLoader
                .getResource(RESOURCES_PATH + "XLEADJ-EXTRAfin.txt").getPath())
                .forEach(l -> {
                    String[] splits = l.split("\t");
                    if (!splits[2].equalsIgnoreCase("noimpl")) {
                        try {
                            adjectivesExtraposed.put(splits[0], new Category(l));
                        } catch (Exception e) {
                            log.error("Unable to split line {} due to ", l, e);
                        }
                    }
                });

    }
}