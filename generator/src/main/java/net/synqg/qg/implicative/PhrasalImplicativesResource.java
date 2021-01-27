package net.synqg.qg.implicative;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.labels.DependencyLabel;
import net.synqg.qg.utils.FileOperationUtils;

import java.util.*;

/**
 * Loads the phrasal implicatives resources which maps verb-noun collocations to their implication categories.
 *
 * @author kaustubhdholé.
 */
@Slf4j
public class PhrasalImplicativesResource {

    private static final String RESOURCES_PATH = "net/synqg/qg/implicative/";
    private final Set<String> negatedForms;
    private Table<String, String, Category> phrasalTable;

    public PhrasalImplicativesResource() {
        ClassLoader classLoader = this
                .getClass()
                .getClassLoader();

        negatedForms = new HashSet<>(Arrays.asList("not", "didnt", "didnt'", "wont", "wont'", "cant",
                "cant'", "aint", "aint'", "neither", "nor", "no", "n't", "never"));

        List<String> verbFamilyList = FileOperationUtils.readLines(classLoader
                .getResource(RESOURCES_PATH + "verb-families.txt").getPath());
        Map<String, List<String>> verbFamilies = loadFamily(verbFamilyList);

        List<String> nounFamilyList = FileOperationUtils.readLines(classLoader
                .getResource(RESOURCES_PATH + "noun-families.txt").getPath());
        Map<String, List<String>> nounFamilies = loadFamily(nounFamilyList);

        List<String> implicativesFile = FileOperationUtils.readLines(classLoader
                .getResource(RESOURCES_PATH + "phrasal-implicatives.txt").getPath());
        phrasalTable = loadPhrasalTable(implicativesFile, verbFamilies, nounFamilies);
        log.info("Loaded Phrasal Implication rules for {} set of verbs and {} set of nouns or {} effective rules",
                verbFamilies.size(), nounFamilies.size(), phrasalTable.size());
    }

    public Optional<EntailedPolarity> getEntailedPolarity(DependencyNode mainClauseVerb,
                                                          DependencyNode mainClauseNoun) {
        if (phrasalTable.contains(mainClauseVerb.lemma(), mainClauseNoun.lemma())) {
            boolean mainClausePolarity = getMainClausePolarity(mainClauseVerb, mainClauseNoun);
            Category category = phrasalTable.get(mainClauseVerb.lemma(), mainClauseNoun.lemma());
            Optional<EntailedPolarity> type = category.toEntailedPolarity(mainClausePolarity);
            return type;
        }
        return Optional.empty();
    }

    /**
     * Gets the polarity of the main clause .
     *
     * @param verb the verb of interest
     * @param noun the noun (mostly the DOBJ) of interest
     * @return polarity of this verb+noun collocation.
     */
    public boolean getMainClausePolarity(DependencyNode verb, DependencyNode noun) {
        // (1) Polarity is positive if it has an even number of negative children like "not" and "never"
        boolean isVerbPositive = (verb.children()
                .stream()
                .filter(x -> negatedForms.contains(x.form().toLowerCase())).count() % 2 == 0);
        // else the lemma == NOT would also work but this has been kept to be safe.

        boolean isNounPositive = (long) noun.childrenWithDepLabel(DependencyLabel.NEG)
                .size() % 2 == 0;

        boolean negativeDeterminer = noun.childrenWithDepLabel(DependencyLabel.DET)
                .stream()
                .filter(d -> d.lemma().equalsIgnoreCase("no"))
                .count() % 2 == 0;

        isNounPositive = isNounPositive == negativeDeterminer;

        return isVerbPositive == isNounPositive;
    }

    private Table<String, String, Category> loadPhrasalTable(List<String> implicativesFile,
                                                             Map<String, List<String>> verbFamilies,
                                                             Map<String, List<String>> nounFamilies) {
        Table<String, String, Category> phrasalTable = HashBasedTable.create();
        implicativesFile.stream()
                .map(line -> line.split("\t"))
                .forEach(split -> {
                    String verbClass = split[0];
                    String nounClass = split[1];
                    if (!verbFamilies.containsKey(verbClass) || !nounFamilies.containsKey(nounClass)) {
                        log.warn("VerbClass {} or NounClass {} not found. ", verbClass, nounClass);
                    } else {
                        String implication = split[2];
                        try {
                            Category category = Category.fromPhrasalImplicativeFormat(implication);
                            for (String verb : verbFamilies.get(verbClass)) {
                                for (String noun : nounFamilies.get(nounClass)) {
                                    phrasalTable.put(verb, noun, category);
                                }
                            }
                        } catch (Exception e) {
                            log.error("Unable to add line {}", split, e);
                        }
                    }
                });
        return phrasalTable;
    }


    private Map<String, List<String>> loadFamily(List<String> familyList) {
        Map<String, List<String>> familyMap = new HashMap<>();
        familyList.stream()
                .map(line -> line.split("\t"))
                .forEach(splits -> {
                    String familyClass = splits[0];
                    List<String> members = new ArrayList<>();
                    members.addAll(Arrays.asList(splits).subList(2, splits.length));
                    familyMap.put(familyClass, members);
                });
        return familyMap;
    }
}
