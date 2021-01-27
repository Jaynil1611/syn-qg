import net.synqg.qg.nlp.normalizer.TextNormalizer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.function.Function;

/**
 * Normalize "I won't" --> "I would not."
 *
 * @author kaustubhdholé.
 */
public class TextNormalizerTest {

    private static Function<String, String> normalizer;

    @BeforeClass
    public static void load() {
        normalizer = new TextNormalizer();
    }

    @Test
    public void testNormalization() {
        String iWill = normalizer.apply("i'll");
        assert iWill.equalsIgnoreCase("I will");
        String heWould = normalizer.apply("he'd");
        assert heWould.equalsIgnoreCase("he would");
    }
}