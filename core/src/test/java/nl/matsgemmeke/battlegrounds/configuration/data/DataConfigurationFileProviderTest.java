package nl.matsgemmeke.battlegrounds.configuration.data;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DataConfigurationFileProviderTest {

    @Spy
    @TempDir
    private File dataFolder;
    @InjectMocks
    private DataConfigurationFileProvider provider;

    @Test
    @DisplayName("get returns YamlConfigurationFile instance with loaded yml file")
    void get() {
        ConfigurationFile configurationFile = provider.get();

        assertThat(configurationFile).isInstanceOf(YamlConfigurationFile.class);
    }
}
