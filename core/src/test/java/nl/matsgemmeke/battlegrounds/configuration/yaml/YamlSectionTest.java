package nl.matsgemmeke.battlegrounds.configuration.yaml;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YamlSectionTest {

    private static final String ABSOLUTE_PATH = "section";
    private static final String PATH = "example.path";
    private static final String KEY = "example-key";
    private static final double EXAMPLE_DOUBLE = 5.5;
    private static final int EXAMPLE_INT = 10;
    private static final String EXAMPLE_STRING = "hello";

    @Mock
    private ConfigurationSection configurationSection;

    private YamlSection yamlSection;

    @BeforeEach
    void setUp() {
        yamlSection = new YamlSection(configurationSection, ABSOLUTE_PATH);
    }

    @ParameterizedTest
    @DisplayName("createSection creates new configuration section")
    @CsvSource({ "'',example.path,example.path", "section,example.path,section.example.path" })
    void createSection(String absolutePath, String childPath, String expectedChildPath) {
        YamlSection yamlSection = new YamlSection(configurationSection, absolutePath);
        Section section = yamlSection.createSection(childPath);

        assertThat(section).isInstanceOf(YamlSection.class);
        assertThat(section.getAbsolutePath()).isEqualTo(expectedChildPath);

        verify(configurationSection).createSection(childPath);
    }

    @ParameterizedTest
    @CsvSource(value = { "test,true", "null,false" }, nullValues = "null")
    @DisplayName("exists returns whether given path exists in configuration section")
    void exists(String pathValue, boolean expectedExists) {
        when(configurationSection.get(PATH)).thenReturn(pathValue);

        boolean exists = yamlSection.exists(PATH);

        assertThat(exists).isEqualTo(expectedExists);
    }

    @Test
    @DisplayName("getDouble returns empty optional when given path does not lead to a double value in configuration section")
    void getDouble_noDouble() {
        when(configurationSection.isDouble(PATH)).thenReturn(false);

        Optional<Double> doubleOptional = yamlSection.getDouble(PATH);

        assertThat(doubleOptional).isEmpty();
    }

    @Test
    @DisplayName("getDouble returns optional with double value from given path")
    void getDouble_successful() {
        when(configurationSection.isDouble(PATH)).thenReturn(true);
        when(configurationSection.getDouble(PATH)).thenReturn(EXAMPLE_DOUBLE);

        Optional<Double> doubleOptional = yamlSection.getDouble(PATH);

        assertThat(doubleOptional).hasValue(EXAMPLE_DOUBLE);
    }

    @Test
    @DisplayName("getInt returns empty optional when given path does not lead to int value in configuration section")
    void getInt_notAnInt() {
        when(configurationSection.isInt(PATH)).thenReturn(false);

        Optional<Integer> intOptional = yamlSection.getInt(PATH);

        assertThat(intOptional).isEmpty();
    }

    @Test
    @DisplayName("getInt returns optional with int value from given path")
    void getInt_successful() {
        when(configurationSection.isInt(PATH)).thenReturn(true);
        when(configurationSection.getInt(PATH)).thenReturn(EXAMPLE_INT);

        Optional<Integer> intOptional = yamlSection.getInt(PATH);

        assertThat(intOptional).hasValue(EXAMPLE_INT);
    }

    @Test
    @DisplayName("getKeys returns unnested keys from configuration section")
    void getKeys() {
        when(configurationSection.getKeys(false)).thenReturn(Set.of(KEY));

        Set<String> keys = yamlSection.getKeys();

        assertThat(keys).containsExactly(KEY);
    }

    @Test
    @DisplayName("getSection returns empty optional when given path does not lead to a configuration section")
    void getSection_notFound() {
        when(configurationSection.getConfigurationSection(PATH)).thenReturn(null);

        Optional<Section> sectionOptional = yamlSection.getSection(PATH);

        assertThat(sectionOptional).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("getSection returns optional with section from given path")
    @CsvSource({ "'',example.path,example.path", "section,example.path,section.example.path" })
    void getSection_successful(String absolutePath, String childPath, String expectedChildPath) {
        ConfigurationSection nestedConfigurationSection = mock(ConfigurationSection.class);

        when(configurationSection.getConfigurationSection(childPath)).thenReturn(nestedConfigurationSection);

        YamlSection yamlSection = new YamlSection(configurationSection, absolutePath);
        Optional<Section> sectionOptional = yamlSection.getSection(childPath);

        assertThat(sectionOptional).hasValueSatisfying(section -> {
            assertThat(section).isInstanceOf(YamlSection.class);
            assertThat(section.getAbsolutePath()).isEqualTo(expectedChildPath);
        });
    }

    @ParameterizedTest
    @DisplayName("getString wraps configuration section value in an Optional")
    @CsvSource(value = { "null,null", "hello,hello" }, nullValues = "null")
    void getString(String configurationValue, String expected) {
        when(configurationSection.getString(PATH)).thenReturn(configurationValue);

        Optional<String> stringOptional = yamlSection.getString(PATH);

        assertThat(stringOptional).isEqualTo(Optional.ofNullable(expected));
    }

    @Test
    @DisplayName("getStringList returns string list from configuration section")
    void getStringList() {
        when(configurationSection.getStringList(PATH)).thenReturn(List.of(EXAMPLE_STRING));

        List<String> stringList = yamlSection.getStringList(PATH);

        assertThat(stringList).containsExactly(EXAMPLE_STRING);
    }

    @ParameterizedTest
    @DisplayName("isList returns whether value at given path is a list in the configuration section")
    @CsvSource({ "true,true", "false,false" })
    void isList(boolean isListInConfigurationSection, boolean expectedResult) {
        when(configurationSection.isList(PATH)).thenReturn(isListInConfigurationSection);

        boolean isList = yamlSection.isList(PATH);

        assertThat(isList).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("removeSection sets given path to null in configuration section")
    void removeSection() {
        yamlSection.removeSection(PATH);

        verify(configurationSection).set(PATH, null);
    }

    @Test
    @DisplayName("set sets value in configuration section")
    void set() {
        yamlSection.set(PATH, EXAMPLE_STRING);

        verify(configurationSection).set(PATH, EXAMPLE_STRING);
    }
}
