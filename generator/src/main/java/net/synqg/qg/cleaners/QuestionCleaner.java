package net.synqg.qg.cleaners;


import net.synqg.qg.service.GeneratedQuestion;

import java.util.Map;
import java.util.function.Function;

/**
 * Filters out questions which are not required.
 * This happens mostly
 * (1) when coreference doesn't work
 * (2) or the templates don't work well.
 * (3) or the document parsing generates bad facts.
 *
 * @author kaustubhdholé.
 */
public interface QuestionCleaner extends Function<Map<GeneratedQuestion, String>, Map<GeneratedQuestion, String>> {
}
