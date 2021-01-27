package net.synqg.qg.service.processors.input;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

import net.synqg.qg.service.processors.Preprocess;


public class Preprocessor implements Preprocess {

    private static final String SPACE = " ";
    private static final String EMPTY = "";
    private static final Map<String, String> UNICODE_STRIP_MAP = ImmutableMap.of("ﬁ", "fi", "ﬂ", "fl");
    private static final Set<String> HTML_TAG_SET = ImmutableSet.of("a", "b", "br", "body", "button", "dir", "div", "font", "hr",
            "html", "p");


    /**
     * Pre-process a message: remove extra whitespace, normalize special unicode characters, remove spaces within HTML tags with no
     * attributes, remove Wikipedia reference numbers.
     *
     * @param message a message.
     * @return a preprocessed message.
     */
    public static String preprocess(@NotNull String message) {
        message = message.replaceAll(" ", SPACE);  // this is not a normal space character
        message = message.replaceAll("\\s+", SPACE);  // normalize multiple spaces to a single space
        message = message.replaceAll("\\[\\s*\\d+\\s*]", EMPTY);  // citation/reference removal
        message = message.replaceAll("[“”]", "\"");
        message = message.replaceAll("–", "-");
        // remove spaces within html tags with no attributes
        // this, however, should not do <a href> --> <ahref>
        for (String htmlTag : HTML_TAG_SET) {
            String regex = "<\\s*" + htmlTag + "\\s*>";
            String replacement = "<" + htmlTag + ">";
            message = message.replaceAll(regex, replacement);
            regex = "<\\s*/\\s*" + htmlTag + "\\s*>";
            replacement = "</" + htmlTag + ">";
            message = message.replaceAll(regex, replacement);
        }

        message = message.replaceAll("(<(.*?)>)\\s*(.*?)\\s*(</\\2>)", "$1$3$4");  // remove spaces between <> </>

        for (Map.Entry<String, String> entry : UNICODE_STRIP_MAP.entrySet()) {
            message = message.replaceAll(entry.getKey(), entry.getValue());
        }
        message = capitalizeFirstLetter(message);
        return message;
    }

    public static String capitalizeFirstLetter(String message) {
        return message.substring(0, 1).toUpperCase() + message.substring(1);
    }

    public String process(String message) {
        return preprocess(message);
    }
}
