package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.TestValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageConfigurationTest {

    private static final String PATH = "path";
    private static final String TYPE = "CHAT";
    private static final String INVALID_TYPE = "invalid type";
    private static final String TEXT_VALUE = "hello";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationFile configurationFile;
    @Mock
    private Logger logger;
    @Mock
    private MessageSpecFactory messageSpecFactory;
    @Spy
    private ObjectValidator objectValidator = TestValidatorFactory.createObjectValidator();
    @InjectMocks
    private LanguageConfiguration languageConfiguration;

    @Test
    @DisplayName("getMessageSpec returns empty optional when given path does not exist")
    void getMessageSpec_nonexistentPath() {
        when(configurationFile.getRootSection().getSection(PATH)).thenReturn(Optional.empty());

        Optional<MessageSpec> messageSpecOptional = languageConfiguration.getMessageSpec(PATH);

        assertThat(messageSpecOptional).isEmpty();
    }

    @Test
    @DisplayName("getMessageSpec returns empty optional when section at given path has no value at field type")
    void getMessageSpec_noTypeValue() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn(Optional.empty());

        when(configurationFile.getRootSection().getSection(PATH)).thenReturn(Optional.of(section));

        Optional<MessageSpec> messageSpecOptional = languageConfiguration.getMessageSpec(PATH);

        assertThat(messageSpecOptional).isEmpty();
    }

    @Test
    @DisplayName("getMessageSpec returns empty optional when section at given path has an invalid type value")
    void getMessageSpec_invalidType() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn(Optional.of(INVALID_TYPE));

        when(configurationFile.getRootSection().getSection(PATH)).thenReturn(Optional.of(section));

        Optional<MessageSpec> messageSpecOptional = languageConfiguration.getMessageSpec(PATH);

        assertThat(messageSpecOptional).isEmpty();
    }

    @Test
    @DisplayName("getMessageSpec returns empty optional when section at given path has an invalid specification")
    void getMessageSpec_invalidSpec() {
        ChatMessageSpec messageSpec = new ChatMessageSpec();
        messageSpec.setLines(Collections.emptyList());

        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn(Optional.of(TYPE));

        when(configurationFile.getRootSection().getSection(PATH)).thenReturn(Optional.of(section));
        when(messageSpecFactory.create(MessageType.CHAT, section)).thenReturn(messageSpec);

        Optional<MessageSpec> messageSpecOptional = languageConfiguration.getMessageSpec(PATH);

        assertThat(messageSpecOptional).isEmpty();

        verify(logger).severe("""
                Attempted to load message at 'path' but was invalid (1 constraint violation):
                 - lines: chat messages must contain at least one line""");
    }

    @Test
    @DisplayName("getMessageSpec returns optional with valid message specification from given path")
    void getMessageSpec_successful() {
        ChatMessageSpec messageSpec = new ChatMessageSpec();
        messageSpec.setLines(List.of("hello"));

        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn(Optional.of(TYPE));

        when(configurationFile.getRootSection().getSection(PATH)).thenReturn(Optional.of(section));
        when(messageSpecFactory.create(MessageType.CHAT, section)).thenReturn(messageSpec);

        Optional<MessageSpec> messageSpecOptional = languageConfiguration.getMessageSpec(PATH);

        assertThat(messageSpecOptional).hasValue(messageSpec);
    }

    @Test
    @DisplayName("getTextValue returns empty optional when given path does not exist")
    void getTextValue_nonexistentPath() {
        when(configurationFile.getRootSection().exists(PATH)).thenReturn(false);

        Optional<String> textValueOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textValueOptional).isEmpty();
    }

    @Test
    @DisplayName("getTextValue returns empty optional when given path leads to empty list")
    void getTextValue_emptyList() {
        when(configurationFile.getRootSection().exists(PATH)).thenReturn(true);
        when(configurationFile.getRootSection().isList(PATH)).thenReturn(true);
        when(configurationFile.getRootSection().getStringList(PATH)).thenReturn(List.of());

        Optional<String> textValueOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textValueOptional).isEmpty();
    }

    @Test
    @DisplayName("getTextValue returns optional with joined strings from list")
    void getTextValue_stringList() {
        when(configurationFile.getRootSection().exists(PATH)).thenReturn(true);
        when(configurationFile.getRootSection().isList(PATH)).thenReturn(true);
        when(configurationFile.getRootSection().getStringList(PATH)).thenReturn(List.of(TEXT_VALUE, "world"));

        Optional<String> textValueOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textValueOptional).hasValue("hello\nworld");
    }

    @Test
    @DisplayName("getTextValue returns optional with string value at given path")
    void getTextValue_singleString() {
        when(configurationFile.getRootSection().exists(PATH)).thenReturn(true);
        when(configurationFile.getRootSection().isList(PATH)).thenReturn(false);
        when(configurationFile.getRootSection().getString(PATH)).thenReturn(Optional.of(TEXT_VALUE));

        Optional<String> textValueOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textValueOptional).hasValue(TEXT_VALUE);
    }
}
