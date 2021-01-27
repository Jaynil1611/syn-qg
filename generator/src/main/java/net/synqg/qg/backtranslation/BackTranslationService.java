package net.synqg.qg.backtranslation;

/**
 * Service which translates an English sentence into German
 * and then back to English.
 *
 * @author kaustubhdholé.
 */
public interface BackTranslationService {

    /**
     * @param sentence "Sometimes using byte pair encoding can be challenging."
     * @return "Sometimes encoding byte pairs can be challenging."
     */
    String backTranslate(String sentence) throws Exception;
}
