package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;

import java.util.List;
import java.util.Optional;

public class LanguageConfiguration {

    private final ConfigurationFile configurationFile;

    public LanguageConfiguration(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    public Optional<String> getTextValue(String path) {
        Section rootSection = configurationFile.getRootSection();

        if (!rootSection.exists(path)) {
            return Optional.empty();
        }

        if (rootSection.isList(path)) {
            List<String> lines = rootSection.getStringList(path);

            if (lines.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(String.join("\n", lines));
        } else {
            return rootSection.getString(path);
        }
    }
}
