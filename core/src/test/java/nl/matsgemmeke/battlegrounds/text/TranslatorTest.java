package nl.matsgemmeke.battlegrounds.text;

import nl.matsgemmeke.battlegrounds.configuration.LanguageConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TranslatorTest {

    private LanguageConfiguration languageConfiguration;

    @Before
    public void setUp() {
        this.languageConfiguration = mock(LanguageConfiguration.class);
    }

    @Test
    public void shouldBeAbleToSetLanguageConfiguration() {
        LanguageConfiguration newOne = mock(LanguageConfiguration.class);

        Translator translator = new Translator(languageConfiguration);
        translator.setLanguageConfiguration(newOne);

        assertEquals(newOne, translator.getLanguageConfiguration());
    }

    @Test(expected = InvalidTranslationKeyException.class)
    public void shouldThrowExceptionWhenTranslatingInvalidKey() {
        Locale locale = Locale.ENGLISH;
        String invalidKey = "invalid";

        when(languageConfiguration.getLocale()).thenReturn(locale);
        when(languageConfiguration.getString(invalidKey)).thenReturn(null);

        Translator translator = new Translator(languageConfiguration);
        translator.translate(invalidKey);
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
