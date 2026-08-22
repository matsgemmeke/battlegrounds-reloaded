package nl.matsgemmeke.battlegrounds.configuration.yaml;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationLoadException;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationSaveException;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class YamlConfigurationFileTest {

    private static final String LOCATION_WORLD = "testworld";
    private static final double LOCATION_X = 1.1;
    private static final double LOCATION_Y = 2.2;
    private static final double LOCATION_Z = 3.3;

    @TempDir
    private File tempDir;

    @Test
    @DisplayName("getRootSection returns YamlSection with yaml configuration")
    void getRootSection() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        Section section = yamlConfigurationFile.getRootSection();

        assertThat(section).isInstanceOf(YamlSection.class);
        assertThat(section.getString("string")).hasValue("words");
    }

    @Test
    @DisplayName("createSection creates and returns new configuration section in yaml configuration")
    void createSection() {
        File yamlFile = new File(tempDir, "test.yml");
        ConfigurationSection section = mock(ConfigurationSection.class);

        YamlConfiguration yamlConfiguration = mock(YamlConfiguration.class);
        when(yamlConfiguration.createSection("test")).thenReturn(section);

        try (MockedStatic<YamlConfiguration> yamlConfigurationStatic = mockStatic(YamlConfiguration.class)) {
            yamlConfigurationStatic.when(() -> YamlConfiguration.loadConfiguration(yamlFile)).thenReturn(yamlConfiguration);

            YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile);
            ConfigurationSection result = yamlConfigurationFile.createSection("test");

            assertThat(result).isEqualTo(section);
        }
    }

    @ParameterizedTest
    @CsvSource({ "string,true", "section,true", "unknown,false" })
    @DisplayName("exists returns whether given path exists in configuration file")
    void exists(String path, boolean expectedExists) throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        boolean exists = yamlConfigurationFile.exists(path);

        assertThat(exists).isEqualTo(expectedExists);
    }

    @Test
    @DisplayName("getConfigurationSection returns empty optional when given path is not a configuration section")
    void getConfigurationSection_noSection() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        Optional<ConfigurationSection> configurationSectionOptional = yamlConfigurationFile.getConfigurationSection("hello");

        assertThat(configurationSectionOptional).isEmpty();
    }

    @Test
    @DisplayName("getConfigurationSection returns optional with corresponding configuration section from given path")
    void getConfigurationSection_successful() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        Optional<ConfigurationSection> configurationSectionOptional = yamlConfigurationFile.getConfigurationSection("section");

        assertThat(configurationSectionOptional).hasValueSatisfying(configurationSection -> configurationSection.get("key").equals("value"));
    }

    @Test
    @DisplayName("getInt returns empty optional when given path is not an int")
    void getInt_notAnInt() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        yamlConfigurationFile.load();

        assertThat(yamlConfigurationFile.getInt("hello")).isEmpty();
    }

    @Test
    @DisplayName("getInt returns optional with int value from given path")
    void getInt_successful() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        yamlConfigurationFile.load();

        assertThat(yamlConfigurationFile.getInt("int")).hasValue(100);
    }

    @Test
    @DisplayName("getStringList returns empty list when given path leads to no list")
    void getStringList_empty() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        List<String> list = yamlConfigurationFile.getStringList("string");

        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("getStringList returns list of strings at the given path")
    void getStringList_successful() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        List<String> list = yamlConfigurationFile.getStringList("string-list");

        assertThat(list).containsExactly("some", "words");
    }

    @ParameterizedTest
    @CsvSource({ "string-list,true", "hello,false" })
    @DisplayName("isList returns whether given path leads to a list value")
    void isList(String path, boolean expected) throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        boolean list = yamlConfigurationFile.isList(path);

        assertThat(list).isEqualTo(expected);
    }

    @Test
    @DisplayName("removeSection sets section path value to null")
    void removeSection() {
        File yamlFile = new File(tempDir, "test.yml");
        YamlConfiguration yamlConfiguration = mock(YamlConfiguration.class);

        try (MockedStatic<YamlConfiguration> yamlConfigurationStatic = mockStatic(YamlConfiguration.class)) {
            yamlConfigurationStatic.when(() -> YamlConfiguration.loadConfiguration(yamlFile)).thenReturn(yamlConfiguration);

            YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile);
            yamlConfigurationFile.removeSection("section");

            verify(yamlConfiguration).set("section", null);
        }
    }

    @Test
    @DisplayName("set sets value in yaml configuration")
    void set() {
        File yamlFile = new File(tempDir, "test.yml");
        YamlConfiguration yamlConfiguration = mock(YamlConfiguration.class);

        try (MockedStatic<YamlConfiguration> yamlConfigurationStatic = mockStatic(YamlConfiguration.class)) {
            yamlConfigurationStatic.when(() -> YamlConfiguration.loadConfiguration(yamlFile)).thenReturn(yamlConfiguration);

            YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile);
            yamlConfigurationFile.set("test", "test");

            verify(yamlConfiguration).set("test", "test");
        }
    }

    @Test
    @DisplayName("setLocation throws IllegalArgumentException when given location has no world")
    void setLocation_noWorld() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);
        Location location = new Location(null, 1, 2, 3);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);

        assertThatThrownBy(() -> yamlConfigurationFile.setLocation("test", location))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("World may not be null");
    }

    @Test
    @DisplayName("setLocation throws IllegalArgumentException when given location has no world")
    void setLocation_successful() {
        File yamlFile = new File(tempDir, "test.yml");

        World world = mock(World.class);
        when(world.getName()).thenReturn(LOCATION_WORLD);

        Location location = new Location(world, LOCATION_X, LOCATION_Y, LOCATION_Z);

        YamlConfiguration yamlConfiguration = mock(YamlConfiguration.class);

        try (MockedStatic<YamlConfiguration> yamlConfigurationStatic = mockStatic(YamlConfiguration.class)) {
            yamlConfigurationStatic.when(() -> YamlConfiguration.loadConfiguration(yamlFile)).thenReturn(yamlConfiguration);

            YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile);
            yamlConfigurationFile.setLocation("test", location);

            verify(yamlConfiguration).set("test.world", LOCATION_WORLD);
            verify(yamlConfiguration).set("test.x", LOCATION_X);
            verify(yamlConfiguration).set("test.y", LOCATION_Y);
            verify(yamlConfiguration).set("test.z", LOCATION_Z);
        }
    }

    @Test
    @DisplayName("load loads yaml configuration without creating copy")
    void load_withoutResource() {
        File yamlFile = new File(tempDir, "test.yml");

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile);

        assertThat(yamlConfigurationFile.getString("hello")).isEmpty();
    }

    @Test
    @DisplayName("load loads yaml configuration from given file and creates new file from resource")
    void load_withResource() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);
        yamlConfigurationFile.load();

        assertThat(yamlConfigurationFile.getString("string")).hasValue("words");
    }

    @Test
    @DisplayName("load throws ConfigurationLoadException when yaml configuration failed to load")
    void load_failed() throws FileNotFoundException {
        File yamlFile = new File(tempDir, "test.yml");
        File resourceFile = new File("src/test/resources/yaml-configuration/test.yml");
        FileInputStream resourceInputStream = new FileInputStream(resourceFile);

        try (MockedStatic<Files> filesStatic = mockStatic(Files.class)) {
            filesStatic.when(() -> Files.copy(resourceInputStream, yamlFile.toPath())).thenThrow(new IOException("error"));

            YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile, resourceInputStream);

            assertThatThrownBy(yamlConfigurationFile::load)
                    .isInstanceOf(ConfigurationLoadException.class)
                    .hasMessage("Failed to load yaml file test.yml");
        }
    }

    @Test
    @DisplayName("save throws ConfigurationSaveException when yaml configuration failed to save")
    void save_failed() throws IOException {
        File yamlFile = new File(tempDir, "test.yml");

        YamlConfiguration yamlConfiguration = mock(YamlConfiguration.class);
        doThrow(new IOException("error")).when(yamlConfiguration).save(yamlFile);

        try (MockedStatic<YamlConfiguration> yamlConfigurationStatic = mockStatic(YamlConfiguration.class)) {
            yamlConfigurationStatic.when(() -> YamlConfiguration.loadConfiguration(yamlFile)).thenReturn(yamlConfiguration);

            YamlConfigurationFile yamlConfigurationFile = new YamlConfigurationFile(yamlFile);

            assertThatThrownBy(yamlConfigurationFile::save)
                    .isInstanceOf(ConfigurationSaveException.class)
                    .hasMessage("Failed to save yaml file test.yml");
        }
    }
}
