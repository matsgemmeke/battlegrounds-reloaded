package nl.matsgemmeke.battlegrounds.configuration.yaml;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationLoadException;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationSaveException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class YamlConfigurationFileTest {

    @TempDir
    private File tempDir;

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

        assertThat(yamlConfigurationFile.getString("hello")).hasValue("world");
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
