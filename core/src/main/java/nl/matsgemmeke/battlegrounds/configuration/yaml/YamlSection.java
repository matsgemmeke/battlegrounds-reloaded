package nl.matsgemmeke.battlegrounds.configuration.yaml;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class YamlSection implements Section {

    private final ConfigurationSection configurationSection;
    private final String absolutePath;

    public YamlSection(ConfigurationSection configurationSection, String absolutePath) {
        this.configurationSection = configurationSection;
        this.absolutePath = absolutePath;
    }

    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public Section createSection(String path) {
        String childAbsolutePath = absolutePath + "." + path;
        return new YamlSection(configurationSection.createSection(path), childAbsolutePath);
    }

    @Override
    public boolean exists(String path) {
        return configurationSection.get(path) != null;
    }

    @Override
    public Optional<Integer> getInt(String path) {
        if (!configurationSection.isInt(path)) {
            return Optional.empty();
        } else {
            return Optional.of(configurationSection.getInt(path));
        }
    }

    @Override
    public Set<String> getKeys() {
        return configurationSection.getKeys(false);
    }

    @Override
    public Optional<Section> getSection(String path) {
        ConfigurationSection childConfigurationSection = configurationSection.getConfigurationSection(path);

        if (childConfigurationSection != null) {
            String childAbsolutePath = absolutePath + "." + path;
            return Optional.of(new YamlSection(childConfigurationSection, childAbsolutePath));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getString(String path) {
        return Optional.ofNullable(configurationSection.getString(path));
    }

    @Override
    public List<String> getStringList(String path) {
        return configurationSection.getStringList(path);
    }

    @Override
    public boolean isList(String path) {
        return configurationSection.isList(path);
    }

    @Override
    public void removeSection(String path) {
        configurationSection.set(path, null);
    }

    @Override
    public void set(String path, Object value) {
        configurationSection.set(path, value);
    }
}
