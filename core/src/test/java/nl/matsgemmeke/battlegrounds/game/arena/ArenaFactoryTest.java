package nl.matsgemmeke.battlegrounds.game.arena;

import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsConfiguration;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsConfigurationFactory;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsSpec;
import nl.matsgemmeke.battlegrounds.game.mapper.ArenaSettingsMapper;
import org.bukkit.plugin.Plugin;
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
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaFactoryTest {

    private static final int ARENA_ID = 1;

    @Mock
    private ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    @Spy
    private ArenaSettingsMapper arenaSettingsMapper;
    @TempDir
    @Spy
    private File arenasFolder;
    @Mock
    private Plugin plugin;
    @InjectMocks
    private ArenaFactory arenaFactory;

    @Test
    @DisplayName("create returns new arena instance and creates a settings file")
    void create() {
        ArenaSettings settings = ArenaSettings.getDefaultSettings();
        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        InputStream resource = InputStream.nullInputStream();

        when(arenaSettingsConfigurationFactory.create(any(File.class), eq(resource))).thenReturn(settingsConfiguration);
        when(plugin.getResource("arenas/settings.yml")).thenReturn(resource);

        Arena arena = arenaFactory.create(ARENA_ID, settings);

        assertThat(arena.getId()).isEqualTo(ARENA_ID);
        assertThat(arena.getSettings()).isNotNull();

        ArgumentCaptor<File> settingsFileCaptor = ArgumentCaptor.forClass(File.class);
        verify(arenaSettingsConfigurationFactory).create(settingsFileCaptor.capture(), eq(resource));

        ArgumentCaptor<ArenaSettingsSpec> settingsSpecCaptor = ArgumentCaptor.forClass(ArenaSettingsSpec.class);
        verify(settingsConfiguration).saveArenaSettings(settingsSpecCaptor.capture());

        assertThat(settingsFileCaptor.getValue().getPath()).endsWith("arena-1" + File.separator + "settings.yml");

        assertThat(settingsSpecCaptor.getValue()).satisfies(spec -> {
            assertThat(spec.lobbyCountdownLength()).isEqualTo(settings.getLobbyCountdownLength());
            assertThat(spec.maxPlayers()).isEqualTo(settings.getMaxPlayers());
            assertThat(spec.minPlayers()).isEqualTo(settings.getMinPlayers());
        });

        verify(settingsConfiguration).load();
    }
}
