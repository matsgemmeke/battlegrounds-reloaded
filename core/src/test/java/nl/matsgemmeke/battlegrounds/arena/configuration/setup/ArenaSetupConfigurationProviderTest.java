package nl.matsgemmeke.battlegrounds.arena.configuration.setup;

import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaSetupConfigurationProviderTest {

    private static final int ARENA_ID = 1;

    @Mock
    private ArenaSetupConfigurationFactory arenaSetupConfigurationFactory;
    @TempDir
    @Spy
    private File arenasFolder;
    @Mock
    private YamlConfigurationFileFactory yamlConfigurationFileFactory;
    @InjectMocks
    private ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;

    @Test
    @DisplayName("get returns ArenaSetupConfiguration with a file location inside the arenas folder")
    void get() {
        YamlConfigurationFile configurationFile = mock(YamlConfigurationFile.class);
        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);

        when(yamlConfigurationFileFactory.create(any(File.class))).thenReturn(configurationFile);
        when(arenaSetupConfigurationFactory.create(configurationFile)).thenReturn(setupConfiguration);

        ArenaSetupConfiguration result = arenaSetupConfigurationProvider.get(ARENA_ID);

        ArgumentCaptor<File> setupFileCaptor = ArgumentCaptor.forClass(File.class);
        verify(yamlConfigurationFileFactory).create(setupFileCaptor.capture());

        assertThat(setupFileCaptor.getValue()).isEqualTo(new File(arenasFolder, "arena-" + ARENA_ID + "/setup.yml"));

        assertThat(result).isEqualTo(setupConfiguration);
    }
}
