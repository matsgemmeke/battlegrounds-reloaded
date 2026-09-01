package nl.matsgemmeke.battlegrounds.arena;

import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsSpec;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaFactoryTest {

    private static final Instant INSTANT = Instant.parse("2026-01-01T12:00:00.00Z");
    private static final int ARENA_ID = 1;
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider;
    @Spy
    private ArenaSettingsMapper arenaSettingsMapper;
    @Mock
    private ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    @Spy
    private Clock clock = Clock.fixed(INSTANT, ZoneOffset.UTC);
    @InjectMocks
    private ArenaFactory arenaFactory;

    @Test
    @DisplayName("create returns new arena instance and creates a settings file")
    void create() {
        ArenaSettings settings = ArenaSettings.getDefaultSettings();
        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);

        when(arenaSettingsConfigurationProvider.get(ARENA_ID)).thenReturn(settingsConfiguration);
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(setupConfiguration);

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
    }
}
