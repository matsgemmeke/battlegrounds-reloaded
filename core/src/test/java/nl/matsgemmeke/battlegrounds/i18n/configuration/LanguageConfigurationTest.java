package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageConfigurationTest {

    private static final String PATH = "path";
    private static final String TEXT_VALUE = "hello";
    private static final List<String> TEXT_VALUES = List.of("good", "morning");

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationFile mainConfigurationFile;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationFile fallbackConfigurationFile;

    private LanguageConfiguration languageConfiguration;

    @BeforeEach
    void setUp() {
        languageConfiguration = new LanguageConfiguration(mainConfigurationFile, fallbackConfigurationFile);
    }

    @Test
    @DisplayName("getTextValue returns optional with single text value from main configuration file")
    void getTextValue_presentAsSingleValueInMainConfigurationFile() {
        when(mainConfigurationFile.getRootSection().exists(PATH)).thenReturn(true);
        when(mainConfigurationFile.getRootSection().isList(PATH)).thenReturn(false);
        when(mainConfigurationFile.getRootSection().getString(PATH)).thenReturn(Optional.of(TEXT_VALUE));

        Optional<String> textOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textOptional).hasValue(TEXT_VALUE);
    }

    @Test
    @DisplayName("getTextValue returns optional with text value joined from list from main configuration file")
    void getTextValue_presentAsListInMainConfigurationFile() {
        when(mainConfigurationFile.getRootSection().exists(PATH)).thenReturn(true);
        when(mainConfigurationFile.getRootSection().isList(PATH)).thenReturn(true);
        when(mainConfigurationFile.getRootSection().getStringList(PATH)).thenReturn(TEXT_VALUES);

        Optional<String> textOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textOptional).hasValue("good\nmorning");
    }

    @Test
    @DisplayName("getTextValue returns empty optional when text value from main configuration file is blank")
    void getTextValue_blankInMainConfigurationFile() {
        when(mainConfigurationFile.getRootSection().exists(PATH)).thenReturn(true);
        when(mainConfigurationFile.getRootSection().isList(PATH)).thenReturn(false);
        when(mainConfigurationFile.getRootSection().getString(PATH)).thenReturn(Optional.of(""));

        Optional<String> textOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textOptional).isEmpty();
    }

    @Test
    @DisplayName("getTextValue returns optional with single text value from fallback configuration file")
    void getTextValue_presentAsSingleValueInFallbackConfigurationFile() {
        when(mainConfigurationFile.getRootSection().exists(PATH)).thenReturn(false);
        when(fallbackConfigurationFile.getRootSection().exists(PATH)).thenReturn(true);
        when(fallbackConfigurationFile.getRootSection().isList(PATH)).thenReturn(false);
        when(fallbackConfigurationFile.getRootSection().getString(PATH)).thenReturn(Optional.of(TEXT_VALUE));

        Optional<String> textOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textOptional).hasValue(TEXT_VALUE);
    }

    @Test
    @DisplayName("getTextValue returns optional with text value joined from list from fallback configuration file")
    void getTextValue_presentAsListInFallbackConfigurationFile() {
        when(mainConfigurationFile.getRootSection().exists(PATH)).thenReturn(false);
        when(fallbackConfigurationFile.getRootSection().exists(PATH)).thenReturn(true);
        when(fallbackConfigurationFile.getRootSection().isList(PATH)).thenReturn(true);
        when(fallbackConfigurationFile.getRootSection().getStringList(PATH)).thenReturn(TEXT_VALUES);

        Optional<String> textOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textOptional).hasValue("good\nmorning");
    }

    @Test
    @DisplayName("getTextValue returns empty optional when both main and fallback configuration file does not contain the given path")
    void getTextValue_absentInBothFiles() {
        when(mainConfigurationFile.getRootSection().exists(PATH)).thenReturn(false);
        when(fallbackConfigurationFile.getRootSection().exists(PATH)).thenReturn(false);

        Optional<String> textOptional = languageConfiguration.getTextValue(PATH);

        assertThat(textOptional).isEmpty();
    }
}
