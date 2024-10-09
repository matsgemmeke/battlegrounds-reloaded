package nl.matsgemmeke.battlegrounds.text;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TextTemplateTest {

    private static final String TEXT = "The quick brown %animal% jumps over the lazy %object%";

    @Test
    public void returnGivenTextWithoutReplacingPlaceholders() {
        TextTemplate template = new TextTemplate(TEXT);
        String result = template.getText();

        assertEquals(TEXT, result);
    }

    @Test
    public void returnGivenTextWithPlaceholdersReplaced() {
        Map<String, Object> values = Map.of(
                "animal", "fox",
                "object", "dog"
        );

        TextTemplate template = new TextTemplate(TEXT);
        String result = template.replace(values);

        assertEquals("The quick brown fox jumps over the lazy dog", result);
    }

    @Test
    public void returnGivenTextAndIgnoreUnknownPlaceholders() {
        Map<String, Object> values = Map.of(
                "animal", "fox",
                "ignore", "dog"
        );

        TextTemplate template = new TextTemplate(TEXT);
        String result = template.replace(values);

        assertEquals("The quick brown fox jumps over the lazy %object%", result);
    }
}
