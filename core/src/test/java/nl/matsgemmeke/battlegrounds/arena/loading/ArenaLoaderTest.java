package nl.matsgemmeke.battlegrounds.arena.loading;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.configuration.ArenaSettingsConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.ArenaSettingsConfigurationFactory;
import nl.matsgemmeke.battlegrounds.arena.configuration.ArenaSettingsSpec;
import nl.matsgemmeke.battlegrounds.arena.configuration.InvalidArenaSettingsSpecException;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.util.ResourceProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaLoaderTest {

    private static final int ARENA_ID = 1;
    private static final int LOBBY_COUNTDOWN_LENGTH = 30;
    private static final int MAX_PLAYERS = 12;
    private static final int MIN_PLAYERS = 2;

    @Mock
    private ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    @Spy
    private ArenaSettingsMapper arenaSettingsMapper;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private ResourceProvider resourceProvider;
    @InjectMocks
    private ArenaLoader arenaLoader;

    @Test
    @DisplayName("loadArena throws InvalidArenaSetupException when failing to load arena settings")
    void loadArena_settingsLoadFail() {
        File arenaFolder = new File("src/test/resources/arena-setups/valid/arena-1");
        InputStream settingsResource = InputStream.nullInputStream();

        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        when(settingsConfiguration.getArenaSettings()).thenThrow(new InvalidArenaSettingsSpecException("error"));

        when(resourceProvider.getResource("arenas/settings.yml")).thenReturn(settingsResource);
        when(arenaSettingsConfigurationFactory.create(any(ConfigurationFile.class))).thenReturn(settingsConfiguration);

        assertThatThrownBy(() -> arenaLoader.loadArena(ARENA_ID, arenaFolder))
                .isInstanceOf(InvalidArenaSetupException.class)
                .hasMessage("Failed to load setup for arena 1");
    }

    @Test
    @DisplayName("loadArena loads content from configuration files and registers new arena instance to the game context provider")
    void loadArena_successful() {
        File arenaFolder = new File("src/test/resources/arena-setups/valid/arena-1");
        InputStream settingsResource = InputStream.nullInputStream();
        ArenaSettingsSpec settingsSpec = new ArenaSettingsSpec(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);

        ArenaSettingsConfiguration settingsConfiguration = mock(ArenaSettingsConfiguration.class);
        when(settingsConfiguration.getArenaSettings()).thenReturn(settingsSpec);

        when(resourceProvider.getResource("arenas/settings.yml")).thenReturn(settingsResource);
        when(arenaSettingsConfigurationFactory.create(any(ConfigurationFile.class))).thenReturn(settingsConfiguration);

        arenaLoader.loadArena(ARENA_ID, arenaFolder);

        ArgumentCaptor<Arena> arenaCaptor = ArgumentCaptor.forClass(Arena.class);
        verify(gameContextProvider).addArena(eq(GameKey.ofArena(ARENA_ID)), arenaCaptor.capture());

        assertThat(arenaCaptor.getValue()).satisfies(arena -> {
            assertThat(arena.getId()).isEqualTo(ARENA_ID);
            assertThat(arena.getSettings()).satisfies(settings -> {
                assertThat(settings.getLobbyCountdownLength()).isEqualTo(LOBBY_COUNTDOWN_LENGTH);
                assertThat(settings.getMaxPlayers()).isEqualTo(MAX_PLAYERS);
                assertThat(settings.getMinPlayers()).isEqualTo(MIN_PLAYERS);
            });
        });
    }
}
