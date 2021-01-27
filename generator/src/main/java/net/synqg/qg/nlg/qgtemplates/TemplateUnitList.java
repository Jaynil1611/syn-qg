package net.synqg.qg.nlg.qgtemplates;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Template and the associated parsing information with it.
 *
 * @author kaustubhdholé.
 */
@Data
@Accessors(fluent = true)
public class TemplateUnitList extends AbstractList<TemplateUnit> {

    private List<TemplateUnit> templateUnits = new ArrayList<>();

    public String formSentence() {
        return templateUnits.stream().map(u -> u.form()).collect(Collectors.joining(" "));
    }

    public String prettyPrint() {
        StringBuilder words = new StringBuilder();
        StringBuilder units = new StringBuilder();
        for (TemplateUnit templateUnit : templateUnits) {
            List<Character> wordChars = convertStringToCharList(templateUnit.form());
            List<Character> unitChars = convertStringToCharList(templateUnit.unit());
            int maxChar = Integer.max(wordChars.size(), unitChars.size());
            wordChars = surroundWith(wordChars, maxChar + 2, ' ', ' ');
            unitChars = surroundWith(unitChars, maxChar + 2, '-', '|');
            String surroundedWord = wordChars.stream().map(c -> c.toString()).collect(Collectors.joining());
            String surroundedUnit = unitChars.stream().map(c -> c.toString()).collect(Collectors.joining());
            words.append(surroundedWord);
            units.append(surroundedUnit);
        }
        return words + "\n" + units;
    }

    private List<Character> surroundWith(List<Character> unitChars, int spanSize,
                                         Character fillingCharacter, Character boundaryCharacter) {
        int halfSize = ((spanSize - unitChars.size()) / 2);
        List<Character> surrounded = new ArrayList<>();
        // Add the initial character
        surrounded.add(boundaryCharacter);
        // Add the left half
        IntStream.range(0, halfSize).mapToObj(i -> fillingCharacter).forEach(surrounded::add);
        // Add the actual text
        surrounded.addAll(unitChars);
        // Add the right half
        IntStream.range(0, halfSize).mapToObj(i -> fillingCharacter).forEach(surrounded::add);
        // Add the terminal character
        surrounded.add(boundaryCharacter);

        return surrounded;
    }

    private List<Character> convertStringToCharList(String str) {
        return new AbstractList<Character>() {
            @Override
            public Character get(int index) {
                return str.charAt(index);
            }

            @Override
            public int size() {
                return str.length();
            }
        };
    }

    @Override
    public TemplateUnit get(int index) {
        return templateUnits.get(index);
    }

    @Override
    public int size() {
        return templateUnits.size();
    }

    public void add(String unit, String form) {
        templateUnits.add(new TemplateUnit(unit, form));
    }
}