package nl.matsgemmeke.battlegrounds.configuration.lang;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageConfigurationTest {

    private static final String PATH = "path";
    private static final String TEXT_VALUE = "hello";

    @Mock
    private ConfigurationFile configurationFile;
    @InjectMocks
    private LanguageConfiguration languageConfiguration;

    @Test
    @DisplayName("getTextValue returns empty optional when given path does not exist")
    void getTextValue_nonexistentPath() {
        when(configurationFile.exists(PATH)).thenReturn(false);

        Optional<String> textValueOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textValueOptional).isEmpty();
    }

    @Test
    @DisplayName("getTextValue returns empty optional when given path leads to empty list")
    void getTextValue_emptyList() {
        when(configurationFile.exists(PATH)).thenReturn(true);
        when(configurationFile.isList(PATH)).thenReturn(true);
        when(configurationFile.getStringList(PATH)).thenReturn(List.of());

        Optional<String> textValueOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textValueOptional).isEmpty();
    }

    @Test
    @DisplayName("getTextValue returns optional with joined strings from list")
    void getTextValue_stringList() {
        when(configurationFile.exists(PATH)).thenReturn(true);
        when(configurationFile.isList(PATH)).thenReturn(true);
        when(configurationFile.getStringList(PATH)).thenReturn(List.of(TEXT_VALUE, "world"));

        Optional<String> textValueOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textValueOptional).hasValue("hello\nworld");
    }

    @Test
    @DisplayName("getTextValue returns optional with string value at given path")
    void getTextValue_singleString() {
        when(configurationFile.exists(PATH)).thenReturn(true);
        when(configurationFile.isList(PATH)).thenReturn(false);
        when(configurationFile.getString(PATH)).thenReturn(Optional.of(TEXT_VALUE));

        Optional<String> textValueOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textValueOptional).hasValue(TEXT_VALUE);
    }
}
