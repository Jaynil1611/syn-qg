package net.synqg.qg.nlg.qgtemplates.nerqgtemplates;

import lombok.Setter;
import net.synqg.qg.nlg.qgtemplates.TemplateUnitList;
import net.synqg.qg.nlp.DependencyNode;
import net.synqg.qg.nlp.NamedEntitySpan;
import net.synqg.qg.nlp.SentenceParse;
import net.synqg.qg.service.QaPair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple substitution based Named Entity Template for different types of NE.
 *
 * @author kaustubhdholé.
 */
public class GenericNerQgTemplate extends AbstractNerQgTemplate {

    private SentenceParse sentenceParse;
    private NamedEntitySpan namedEntitySpan;
    @Setter
    private String whTerm;

    public GenericNerQgTemplate(SentenceParse sentenceParse, NamedEntitySpan namedEntitySpan) {
        this.sentenceParse = sentenceParse;
        this.namedEntitySpan = namedEntitySpan;
    }

    @Override
    public List<QaPair> generateQuestion() {

        List<QaPair> qaPairs = new ArrayList<>();
        TemplateUnitList templateUnits = new TemplateUnitList();

        List<DependencyNode> tokens = removePunctuation(sentenceParse.dependencyNodes());

        // Before the Span
        templateUnits.add("Other Terms", tokens
                .subList(0, namedEntitySpan.startIndex())
                .stream().map(DependencyNode::form)
                .collect(Collectors.joining(" ")));

        // The whQuestion in place of the NE Span
        templateUnits.add(namedEntitySpan.type().val, whTerm);

        // After the Span
        templateUnits.add("Other Terms", tokens
                .subList(namedEntitySpan.endIndex(), tokens.size())
                .stream().map(DependencyNode::form)
                .collect(Collectors.joining(" ")));

        // The NE span
        String answer = namedEntitySpan.form();

        qaPairs.add(new QaPair(templateUnits.formSentence(), answer, templateName(), templateUnits));
        return qaPairs;
    }

    /**
     * A deep copy is not generated. Replace/Add a question mark depNode with the appropriate NE type.
     */
    private List<DependencyNode> removePunctuation(List<DependencyNode> dependencyNodes) {
        List<DependencyNode> tokens = new ArrayList<>(dependencyNodes);
        DependencyNode lastToken = tokens.get(tokens.size() - 1);
        if (lastToken.pos().isPunctuation()) {
            tokens = tokens.subList(0, tokens.size() - 1);
            tokens.add(new DependencyNode()
                    .head(lastToken.head())
                    .form("?")
                    .lemma("?")
                    .namedEntityType(lastToken.namedEntityType()));
            return tokens;
        } else {
            // add a new token to the existing list
            tokens.add(new DependencyNode()
                    .head(lastToken.head())
                    .form("?")
                    .lemma("?"));
            return tokens;
        }
    }
}
