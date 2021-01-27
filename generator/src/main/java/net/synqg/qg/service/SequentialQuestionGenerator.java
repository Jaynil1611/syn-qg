package net.synqg.qg.service;

import com.google.common.collect.ImmutableList;
import net.synqg.qg.nlg.factories.DependencyQuestionGeneratorFactory;
import net.synqg.qg.nlg.factories.NerQuestionGeneratorFactory;
import net.synqg.qg.nlg.factories.QuestionGeneratorFactory;
import net.synqg.qg.nlg.factories.SrlQuestionGeneratorFactory;
import net.synqg.qg.nlg.qgtemplates.QgTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * All the set of question generators.
 *
 * @author kaustubhdholé.
 */
public class SequentialQuestionGenerator implements QuestionGeneratorFactory {

    private List<QuestionGeneratorFactory> generators;

    public SequentialQuestionGenerator() {
        generators = ImmutableList.of(
                new DependencyQuestionGeneratorFactory(),
                new SrlQuestionGeneratorFactory(),
                new NerQuestionGeneratorFactory());
    }

    @Override
    public List<QgTemplate> extractTemplate(IQgAnalysis qgAnalysis) {
        return generators.stream()
                .map(g -> g.extractTemplate(qgAnalysis))
                .flatMap(t -> t.stream())
                .collect(Collectors.toList());
    }
}
