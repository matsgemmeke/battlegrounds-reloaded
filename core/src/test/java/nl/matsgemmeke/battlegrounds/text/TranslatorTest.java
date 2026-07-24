package nl.matsgemmeke.battlegrounds.text;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.text.configuration.LanguageConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslatorTest {

    private static final String LANGUAGE = "en";
    private static final String TRANSLATION_KEY = "key";
    private static final String TEXT_VALUE = "&aColorful text";

    @Mock
    private BattlegroundsConfiguration battlegroundsConfiguration;
    @Mock
    private LanguageConfiguration languageConfiguration;
    @InjectMocks
    private Translator translator;

    @Test
    @DisplayName("translate throws InvalidTranslationKeyException when given key is not a path to a valid text value")
    void translate_invalidKey() {
        when(battlegroundsConfiguration.getLanguage()).thenReturn(LANGUAGE);
        when(languageConfiguration.getTextValue(TRANSLATION_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> translator.translate(TRANSLATION_KEY))
                .isInstanceOf(InvalidTranslationKeyException.class)
                .hasMessage("Translation for key \"key\" in language configuration 'en' not found");
    }

    @Test
    @DisplayName("translate returns text template with the text value from given path, formatted as ChatColor messages")
    void translate_successful() {
        when(languageConfiguration.getTextValue(TRANSLATION_KEY)).thenReturn(Optional.of(TEXT_VALUE));

        TextTemplate textTemplate = translator.translate(TRANSLATION_KEY);

        assertThat(textTemplate.getText()).isEqualTo("§aColorful text");
    }
}
