package net.synqg.qg.nlg.factories;

import net.synqg.qg.nlg.qgtemplates.QgTemplate;

import net.synqg.qg.service.IQgAnalysis;

import java.util.List;

/**
 * @author viswa
 */
public interface QuestionGeneratorFactory {

    List<QgTemplate> extractTemplate(IQgAnalysis qgAnalysis);

}
