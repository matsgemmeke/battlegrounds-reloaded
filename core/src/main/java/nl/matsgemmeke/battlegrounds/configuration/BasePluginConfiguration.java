package nl.matsgemmeke.battlegrounds.configuration;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public abstract class BasePluginConfiguration implements PluginConfiguration {

    private final boolean readOnly;
    @NotNull
    private final File file;
    @Nullable
    private final InputStream resource;
    private YamlConfiguration yamlConfiguration;

    public BasePluginConfiguration(@NotNull File file, @Nullable InputStream resource) {
        this(file, resource, false);
    }

    public BasePluginConfiguration(@NotNull File file, @Nullable InputStream resource, boolean readOnly) {
        this.file = file;
        this.resource = resource;
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @NotNull
    public ConfigurationSection createSection(@NotNull String path) {
        return yamlConfiguration.createSection(path);
    }

    @Nullable
    public Object get(@NotNull String path) {
        return yamlConfiguration.get(path);
    }

    public boolean getBoolean(@NotNull String path) {
        return yamlConfiguration.getBoolean(path);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        return yamlConfiguration.getBoolean(path, def);
    }

    public double getDouble(@NotNull String path) {
        return yamlConfiguration.getDouble(path);
    }

    public double getDouble(@NotNull String path, double def) {
        return yamlConfiguration.getDouble(path, def);
    }

    public int getInteger(@NotNull String path) {
        return yamlConfiguration.getInt(path);
    }

    public int getInteger(@NotNull String path, int def) {
        return yamlConfiguration.getInt(path, def);
    }

    public Optional<Double> getOptionalDouble(String route) {
        return Optional.ofNullable(yamlConfiguration.getObject(route, Double.class));
    }

    public Optional<ConfigurationSection> getOptionalSection(String route) {
        return Optional.ofNullable(yamlConfiguration.getConfigurationSection(route));
    }

    public Configuration getRoot() {
        return yamlConfiguration.getRoot();
    }

    @Nullable
    public ConfigurationSection getSection(@NotNull String path) {
        return yamlConfiguration.getConfigurationSection(path);
    }

    @Nullable
    public String getString(@NotNull String path) {
        return yamlConfiguration.getString(path);
    }

    @Nullable
    public String getString(@NotNull String path, @Nullable String def) {
        return yamlConfiguration.getString(path, def);
    }

    public Map<String, Object> getValues(String path) {
        Object obj = this.get(path);

        if (!(obj instanceof Map map)) {
            return Collections.emptyMap();
        }

        return map;
    }

    public boolean load() {
        try {
            if (!file.exists() && resource != null) {
                file.getParentFile().mkdirs();

                try (InputStream in = resource) {
                    Files.copy(in, file.toPath());
                }
            }

            yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            return true;
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try {
            yamlConfiguration.save(file);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void setValue(@NotNull String path, @NotNull Object value) {
        if (readOnly) {
            throw new UnsupportedOperationException("Configuration file is read-only");
        }

        yamlConfiguration.set(path, value);
    }
}
