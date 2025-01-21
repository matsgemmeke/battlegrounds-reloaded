package nl.matsgemmeke.battlegrounds.text;

import nl.matsgemmeke.battlegrounds.configuration.lang.LanguageConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TranslatorTest {

    private LanguageConfiguration languageConfiguration;

    @BeforeEach
    public void setUp() {
        this.languageConfiguration = mock(LanguageConfiguration.class);
    }

    @Test
    public void shouldThrowExceptionWhenTranslatingInvalidKey() {
        Locale locale = Locale.ENGLISH;
        String invalidKey = "invalid";

        when(languageConfiguration.getLocale()).thenReturn(locale);
        when(languageConfiguration.getString(invalidKey)).thenReturn(null);

        Translator translator = new Translator(languageConfiguration);

        assertThrows(InvalidTranslationKeyException.class, () -> translator.translate(invalidKey));
    }

    @Test
    public void shouldReturnValueFromLanguageConfiguration() {
        String translation = "hello";
        String translationKey = "key";

        when(languageConfiguration.getString(translationKey)).thenReturn(translation);

        Translator translator = new Translator(languageConfiguration);
        String result = translator.translate(translationKey).getText();

        assertEquals("hello", result);
    }
}
