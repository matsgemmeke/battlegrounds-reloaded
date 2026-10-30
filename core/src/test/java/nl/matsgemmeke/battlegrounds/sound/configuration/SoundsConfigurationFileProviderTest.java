package nl.matsgemmeke.battlegrounds.sound.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import nl.matsgemmeke.battlegrounds.util.ResourceProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoundsConfigurationFileProviderTest {

    @Spy
    @TempDir
    private File dataFolder;
    @Mock
    private ResourceProvider resourceProvider;
    @Mock
    private YamlConfigurationFileFactory yamlConfigurationFileFactory;
    @InjectMocks
    private SoundsConfigurationFileProvider provider;

    @Test
    @DisplayName("get returns YamlConfigurationFile instance with loaded sounds yml file")
    void get() {
        InputStream soundsResource = InputStream.nullInputStream();
        YamlConfigurationFile yamlConfigurationFile = mock(YamlConfigurationFile.class);

        when(resourceProvider.getResource("sounds.yml")).thenReturn(soundsResource);
        when(yamlConfigurationFileFactory.create(new File(dataFolder, "sounds.yml"), soundsResource)).thenReturn(yamlConfigurationFile);

        ConfigurationFile configurationFile = provider.get();

        assertThat(configurationFile).isEqualTo(yamlConfigurationFile);

        verify(yamlConfigurationFile).load();
    }
}
