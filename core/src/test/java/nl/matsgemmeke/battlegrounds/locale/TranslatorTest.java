package nl.matsgemmeke.battlegrounds.locale;

import nl.matsgemmeke.battlegrounds.configuration.LanguageConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
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
        String result = translator.translate(translationKey);

        assertEquals("hello", result);
    }

    @Test
    public void shouldReturnValueFromLanguageConfigurationAndApplySinglePlaceholder() {
        String translation = "hello %test%";
        String translationKey = "key";

        when(languageConfiguration.getString(translationKey)).thenReturn(translation);

        Translator translator = new Translator(languageConfiguration);
        PlaceholderEntry placeholder = new PlaceholderEntry("test", "world");

        String result = translator.translate(translationKey, placeholder);

        assertEquals("hello world", result);
    }

    @Test
    public void shouldReturnValueFromLanguageConfigurationAndApplyMultiplePlaceholders() {
        String translation = "%hello% %test%";
        String translationKey = "key";

        when(languageConfiguration.getString(translationKey)).thenReturn(translation);

        Translator translator = new Translator(languageConfiguration);
        List<PlaceholderEntry> placeholders = Arrays.asList(new PlaceholderEntry("hello", "hola"), new PlaceholderEntry("test", "mundo"));

        String result = translator.translate(translationKey, placeholders);

        assertEquals("hola mundo", result);
    }
}
