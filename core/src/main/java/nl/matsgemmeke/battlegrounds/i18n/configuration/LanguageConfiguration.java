package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;

import java.util.Optional;

public class LanguageConfiguration {

    private final ConfigurationFile mainConfigurationFile;
    private final ConfigurationFile fallbackConfigurationFile;

    public LanguageConfiguration(ConfigurationFile mainConfigurationFile, ConfigurationFile fallbackConfigurationFile) {
        this.mainConfigurationFile = mainConfigurationFile;
        this.fallbackConfigurationFile = fallbackConfigurationFile;
    }

    public Optional<String> getTextValue(String path) {
        String value = this.lookup(path, mainConfigurationFile).orElse(null);

        if (value == null) {
            return this.lookup(path, fallbackConfigurationFile);
        }

        if (value.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(value);
    }

    private Optional<String> lookup(String path, ConfigurationFile configurationFile) {
        Section rootSection = configurationFile.getRootSection();

        if (!rootSection.exists(path)) {
            return Optional.empty();
        }

        if (rootSection.isList(path)) {
            return Optional.of(String.join("\n", rootSection.getStringList(path)));
        }

        return Optional.of(rootSection.getString(path).orElse(""));
    }
}
