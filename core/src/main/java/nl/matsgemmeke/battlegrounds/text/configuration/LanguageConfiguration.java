package nl.matsgemmeke.battlegrounds.text.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;

import java.util.List;
import java.util.Optional;

public class LanguageConfiguration {

    private final ConfigurationFile configurationFile;

    public LanguageConfiguration(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    public Optional<String> getTextValue(String path) {
        if (!configurationFile.exists(path)) {
            return Optional.empty();
        }

        if (configurationFile.isList(path)) {
            List<String> lines = configurationFile.getStringList(path);

            if (lines.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(String.join("\n", lines));
        } else {
            return configurationFile.getString(path);
        }
    }
}
