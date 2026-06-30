package nl.matsgemmeke.battlegrounds.game.arena;

import nl.matsgemmeke.battlegrounds.game.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.configuration.*;
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
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaFactoryTest {

    private static final Instant INSTANT = Instant.parse("2026-01-01T12:00:00.00Z");
    private static final int ARENA_ID = 1;
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    @Spy
    private ArenaSettingsMapper arenaSettingsMapper;
    @Mock
    private ArenaSetupConfigurationFactory arenaSetupConfigurationFactory;
    @Spy
    private Clock clock = Clock.fixed(INSTANT, ZoneOffset.UTC);
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
        InputStream resource = InputStream.nullInputStream();
        ArenaSettings settings = ArenaSettings.getDefaultSettings();
        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);

        when(plugin.getResource("arenas/settings.yml")).thenReturn(resource);
        when(arenaSettingsConfigurationFactory.create(any(File.class), eq(resource))).thenReturn(settingsConfiguration);
        when(arenaSetupConfigurationFactory.create(any(File.class))).thenReturn(setupConfiguration);

        Arena arena = arenaFactory.create(ARENA_ID, settings, PLAYER_ID);

        assertThat(arena.getId()).isEqualTo(ARENA_ID);
        assertThat(arena.getSettings()).isNotNull();

        ArgumentCaptor<File> settingsFileCaptor = ArgumentCaptor.forClass(File.class);
        verify(arenaSettingsConfigurationFactory).create(settingsFileCaptor.capture(), eq(resource));

        ArgumentCaptor<ArenaSettingsSpec> settingsSpecCaptor = ArgumentCaptor.forClass(ArenaSettingsSpec.class);
        verify(settingsConfiguration).saveArenaSettings(settingsSpecCaptor.capture());

        ArgumentCaptor<File> setupFileCaptor = ArgumentCaptor.forClass(File.class);
        verify(arenaSetupConfigurationFactory).create(setupFileCaptor.capture());

        assertThat(settingsFileCaptor.getValue().getPath()).endsWith("arena-1" + File.separator + "settings.yml");

        assertThat(settingsSpecCaptor.getValue()).satisfies(spec -> {
            assertThat(spec.lobbyCountdownLength()).isEqualTo(settings.getLobbyCountdownLength());
            assertThat(spec.maxPlayers()).isEqualTo(settings.getMaxPlayers());
            assertThat(spec.minPlayers()).isEqualTo(settings.getMinPlayers());
        });

        assertThat(setupFileCaptor.getValue().getPath()).endsWith("arena-1" + File.separator + "setup.yml");

        verify(settingsConfiguration).load();
        verify(setupConfiguration).setCreatedAt(INSTANT);
        verify(setupConfiguration).setCreatedBy(PLAYER_ID);
        verify(setupConfiguration).save();
    }
}
