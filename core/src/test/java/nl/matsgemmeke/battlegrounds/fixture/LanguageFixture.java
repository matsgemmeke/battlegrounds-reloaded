package nl.matsgemmeke.battlegrounds.fixture;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class LanguageFixture {

    private static final String RESOURCE_PATH = "/lang/lang_en.yml";
    private static Map<String, Object> translations;

    private LanguageFixture() {
    }

    public static String getTranslation(String translationKey) {
        if (translations == null) {
            load();
        }

        Object value = resolve(translationKey);

        if (value == null) {
            throw new IllegalArgumentException("No translation found for key: " + translationKey);
        }

        return String.valueOf(value);
    }

    private static void load() {
        try (InputStream input = LanguageFixture.class.getResourceAsStream(RESOURCE_PATH)) {
            if (input == null) {
                throw new IllegalStateException("Could not find language file at " + RESOURCE_PATH);
            }

            Yaml yaml = new Yaml();
            translations = yaml.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load language file at " + RESOURCE_PATH, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object resolve(String translationKey) {
        String[] parts = translationKey.split("\\.");
        Map<String, Object> current = translations;
        Object value = null;

        for (int i = 0; i < parts.length; i++) {
            value = current.get(parts[i]);

            if (i < parts.length - 1) {
                if (!(value instanceof Map)) {
                    return null;
                }

                current = (Map<String, Object>) value;
            }
        }

        return value;
    }
}
