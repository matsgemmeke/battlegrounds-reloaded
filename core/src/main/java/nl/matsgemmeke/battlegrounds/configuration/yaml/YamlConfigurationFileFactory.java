package nl.matsgemmeke.battlegrounds.configuration.yaml;

import java.io.File;
import java.io.InputStream;

public class YamlConfigurationFileFactory {

    public YamlConfigurationFile create(File file) {
        return new YamlConfigurationFile(file);
    }

    public YamlConfigurationFile create(File file, InputStream resource) {
        return new YamlConfigurationFile(file, resource);
    }
}
