package nl.matsgemmeke.battlegrounds.arena;

import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsConfigurationFactory;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsSpec;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationResolver;
import nl.matsgemmeke.battlegrounds.arena.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
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
    private ArenaSetupConfigurationResolver arenaSetupConfigurationResolver;
    @Spy
    private Clock clock = Clock.fixed(INSTANT, ZoneOffset.UTC);
    @TempDir
    @Spy
    private File arenasFolder;
    @Mock
    private Plugin plugin;
    @Mock
    private YamlConfigurationFileFactory yamlConfigurationFileFactory;
    @InjectMocks
    private ArenaFactory arenaFactory;

    @Test
    @DisplayName("create returns new arena instance and creates a settings file")
    void create() {
        InputStream resource = InputStream.nullInputStream();
        ArenaSettings settings = ArenaSettings.getDefaultSettings();
        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);
        YamlConfigurationFile settingsConfigurationFile = mock(YamlConfigurationFile.class);

        when(plugin.getResource("arenas/settings.yml")).thenReturn(resource);
        when(yamlConfigurationFileFactory.create(new File(arenasFolder, "arena-1" + File.separator + "settings.yml"), resource)).thenReturn(settingsConfigurationFile);
        when(arenaSettingsConfigurationFactory.create(any(ConfigurationFile.class))).thenReturn(settingsConfiguration);
        when(arenaSetupConfigurationResolver.resolve(ARENA_ID)).thenReturn(setupConfiguration);

        Arena arena = arenaFactory.create(ARENA_ID, settings, PLAYER_ID);

        assertThat(arena.getId()).isEqualTo(ARENA_ID);
        assertThat(arena.getSettings()).isNotNull();

        ArgumentCaptor<ArenaSettingsSpec> settingsSpecCaptor = ArgumentCaptor.forClass(ArenaSettingsSpec.class);
        verify(settingsConfiguration).saveArenaSettings(settingsSpecCaptor.capture());

        assertThat(settingsSpecCaptor.getValue()).satisfies(spec -> {
            assertThat(spec.lobbyCountdownLength()).isEqualTo(settings.getLobbyCountdownLength());
            assertThat(spec.maxPlayers()).isEqualTo(settings.getMaxPlayers());
            assertThat(spec.minPlayers()).isEqualTo(settings.getMinPlayers());
        });

        verify(setupConfiguration).setCreatedAt(INSTANT);
        verify(setupConfiguration).setCreatedBy(PLAYER_ID);
        verify(setupConfiguration).save();
    }
}
