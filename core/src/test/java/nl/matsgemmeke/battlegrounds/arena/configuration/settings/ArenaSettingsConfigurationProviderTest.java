package nl.matsgemmeke.battlegrounds.arena.configuration.settings;

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
class ArenaSettingsConfigurationProviderTest {

    private static final int ARENA_ID = 1;

    @Mock
    private ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    @TempDir
    @Spy
    private File arenasFolder;
    @Mock
    private ResourceProvider resourceProvider;
    @Mock
    private YamlConfigurationFileFactory yamlConfigurationFileFactory;
    @InjectMocks
    private ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider;

    @Test
    @DisplayName("get returns ArenaSettingsConfiguration with a file location inside the arenas folder, and the corresponding resource")
    void get() {
        InputStream resource = InputStream.nullInputStream();
        YamlConfigurationFile configurationFile = mock(YamlConfigurationFile.class);
        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);

        when(resourceProvider.getResource("arenas/settings.yml")).thenReturn(resource);
        when(yamlConfigurationFileFactory.create(new File(arenasFolder, "arena-" + ARENA_ID + "/settings.yml"), resource)).thenReturn(configurationFile);
        when(arenaSettingsConfigurationFactory.create(configurationFile)).thenReturn(settingsConfiguration);

        ArenaSettingsConfiguration result = arenaSettingsConfigurationProvider.get(ARENA_ID);

        assertThat(result).isEqualTo(settingsConfiguration);
    }
}
