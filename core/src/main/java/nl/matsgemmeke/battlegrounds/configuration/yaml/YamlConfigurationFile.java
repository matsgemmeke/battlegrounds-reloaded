package nl.matsgemmeke.battlegrounds.configuration.yaml;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationLoadException;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationSaveException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

public class YamlConfigurationFile implements ConfigurationFile {

    private final File file;
    @Nullable
    private final InputStream resource;
    @Nullable
    private YamlConfiguration yamlConfiguration;

    public YamlConfigurationFile(File file) {
        this(file, null);
    }

    public YamlConfigurationFile(File file, @Nullable InputStream resource) {
        this.file = file;
        this.resource = resource;
    }

    @Override
    public ConfigurationSection createSection(String path) {
        YamlConfiguration yamlConfiguration = this.getYamlConfiguration();
        return yamlConfiguration.createSection(path);
    }

    @Override
    public Optional<String> getString(String path) {
        YamlConfiguration yamlConfiguration = this.getYamlConfiguration();
        return Optional.ofNullable(yamlConfiguration.getString(path));
    }

    @Override
    public void set(String path, Object value) {
        YamlConfiguration yamlConfiguration = this.getYamlConfiguration();
        yamlConfiguration.set(path, value);
    }

    @Override
    public void load() {
        try {
            if (!file.exists() && resource != null) {
                file.getParentFile().mkdirs();

                try (InputStream in = resource) {
                    Files.copy(in, file.toPath());
                }
            }

            yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        } catch (IOException | IllegalArgumentException ex) {
            throw new ConfigurationLoadException("Failed to load yaml file %s".formatted(file.getName()), ex);
        }
    }

    @Override
    public void save() {
        YamlConfiguration yamlConfiguration = this.getYamlConfiguration();

        try {
            yamlConfiguration.save(file);
        } catch (IOException ex) {
            throw new ConfigurationSaveException("Failed to save yaml file %s".formatted(file.getName()), ex);
        }
    }

    private YamlConfiguration getYamlConfiguration() {
        if (yamlConfiguration == null) {
            this.load();
        }

        return yamlConfiguration;
    }
}
