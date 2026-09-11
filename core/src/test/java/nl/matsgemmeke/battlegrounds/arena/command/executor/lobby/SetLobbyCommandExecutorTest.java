package nl.matsgemmeke.battlegrounds.arena.command.executor.lobby;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetLobbyCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String LOBBY_SET_FAILED_TEXT = LanguageFixture.getTranslation(TranslationKey.LOBBY_SET_FAILED.getPath());
    private static final String LOBBY_SET_SUCCESSFUL_TEXT = LanguageFixture.getTranslation(TranslationKey.LOBBY_SET_SUCCESSFUL.getPath());

    private static final String PLAYER_NAME = "TestPlayer";
    private static final String PLAYER_LOCATION_WORLD = "world";
    private static final double PLAYER_LOCATION_X = 1.1;
    private static final double PLAYER_LOCATION_Y = 2.2;
    private static final double PLAYER_LOCATION_Z = 3.3;
    private static final float PLAYER_LOCATION_YAW = 90.0f;
    private static final float PLAYER_LOCATION_PITCH = 0.0f;

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private Logger logger;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private SetLobbyCommandExecutor commandExecutor;
    @Captor
    private ArgumentCaptor<Location> locationCaptor;

    @Test
    @DisplayName("execute logs warning message when the validated arena id is somehow not registered")
    void execute_invalidArenaId() {
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(translator.translate(TranslationKey.LOBBY_SET_FAILED.getPath())).thenReturn(new TextTemplate(LOBBY_SET_FAILED_TEXT));

        commandExecutor.execute(player, ARENA_ID);

        verify(logger).warning("Player TestPlayer attempted to set the lobby for arena 1, however no arena was found for this validated arena id");
        verify(player).sendMessage(LOBBY_SET_FAILED_TEXT);
    }

    @Test
    @DisplayName("execute sets the arena lobby to the player's current location")
    void execute_successful() {
        Arena arena = mock(Arena.class);
        ArenaSetupConfiguration setupConfiguration = mock(ArenaSetupConfiguration.class);
        World world = mock(World.class);
        Location playerLocation = new Location(world, PLAYER_LOCATION_X, PLAYER_LOCATION_Y, PLAYER_LOCATION_Z, PLAYER_LOCATION_YAW, PLAYER_LOCATION_PITCH);
        LocationData lobbyLocationData = new LocationData(PLAYER_LOCATION_WORLD, PLAYER_LOCATION_X, PLAYER_LOCATION_Y, PLAYER_LOCATION_Z, PLAYER_LOCATION_YAW, PLAYER_LOCATION_PITCH);

        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));
        when(player.getLocation()).thenReturn(playerLocation);
        when(locationMapper.toLocationData(any(Location.class))).thenReturn(lobbyLocationData);
        when(arenaSetupConfigurationProvider.get(ARENA_ID)).thenReturn(setupConfiguration);
        when(translator.translate(TranslationKey.LOBBY_SET_SUCCESSFUL.getPath())).thenReturn(new TextTemplate(LOBBY_SET_SUCCESSFUL_TEXT));

        commandExecutor.execute(player, ARENA_ID);

        verify(arena).setLobbyLocation(locationCaptor.capture());

        assertThat(locationCaptor.getValue()).satisfies(location -> {
            assertThat(location.getWorld()).isEqualTo(world);
            assertThat(location.getX()).isEqualTo(1.5);
            assertThat(location.getY()).isEqualTo(PLAYER_LOCATION_Y);
            assertThat(location.getZ()).isEqualTo(3.5);
            assertThat(location.getYaw()).isEqualTo(PLAYER_LOCATION_YAW);
            assertThat(location.getPitch()).isEqualTo(PLAYER_LOCATION_PITCH);
        });

        verify(setupConfiguration).setLobby(lobbyLocationData);
        verify(player).sendMessage("&6You have set the waiting lobby of arena &f1&6 to your current location.");
    }
}
