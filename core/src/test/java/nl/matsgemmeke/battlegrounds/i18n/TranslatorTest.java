package nl.matsgemmeke.battlegrounds.i18n;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.configuration.LanguageConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslatorTest {

    private static final String LANGUAGE = "en";
    private static final String TRANSLATION_KEY = "key";
    private static final String TEXT_VALUE = "&aColorful text";
    private static final String TEXT = "<gold>This is a message for player <player>";

    @Mock
    private BattlegroundsConfiguration battlegroundsConfiguration;
    @Mock
    private LanguageConfiguration languageConfiguration;
    @Spy
    private MiniMessage miniMessage = MiniMessage.miniMessage();
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

    @Test
    @DisplayName("translateMessage returns empty optional when the language configuration has no value at the given translation key")
    void translateMessage_noValueAtTranslationKey() {
        when(languageConfiguration.getTextValue(TRANSLATION_KEY)).thenReturn(Optional.empty());

        Optional<Message> messageOptional = translator.translateMessage(TRANSLATION_KEY);

        assertThat(messageOptional).isEmpty();
    }

    @Test
    @DisplayName("translateMessage returns optional with deserialized component")
    void translateMessage_successful() {
        Replacement[] replacements = { Replacement.of("player", "TestPlayer") };
        Audience audience = mock(Audience.class);

        when(languageConfiguration.getTextValue(TRANSLATION_KEY)).thenReturn(Optional.of(TEXT));

        translator.translateMessage(TRANSLATION_KEY, replacements).ifPresent(message -> message.send(audience));

        ArgumentCaptor<TextComponent> componentCaptor = ArgumentCaptor.forClass(TextComponent.class);
        verify(audience).sendMessage(componentCaptor.capture());

        assertThat(miniMessage.serialize(componentCaptor.getValue())).isEqualTo("<gold>This is a message for player TestPlayer");
    }

    @Test
    @DisplayName("translateText returns empty optional when the language configuration has no value at the given translation key")
    void translateText_unknownTranslationKey() {
        when(languageConfiguration.getTextValue(TRANSLATION_KEY)).thenReturn(Optional.empty());

        Optional<String> optionalString = translator.translateText(TRANSLATION_KEY);

        assertThat(optionalString).isEmpty();
    }

    @Test
    @DisplayName("translateText returns optional with corresponding text value")
    void translateText_successful() {
        when(languageConfiguration.getTextValue(TRANSLATION_KEY)).thenReturn(Optional.of(TEXT));

        Optional<String> optionalString = translator.translateText(TRANSLATION_KEY);

        assertThat(optionalString).hasValue(TEXT);
    }
}
