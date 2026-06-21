package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.spawn.RespawnHandler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerRespawnEventHandlerTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final GameKey GAME_KEY = GameKey.ofFreeplay();

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Provider<RespawnHandler> respawnHandlerProvider;
    @InjectMocks
    private PlayerRespawnEventHandler eventHandler;

    @Test
    @DisplayName("handle does not alter event when player is not in a game context")
    void handle_playerNotInGameContext() {
        Location respawnLocation = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        eventHandler.handle(event);

        assertThat(event.getRespawnLocation()).isEqualTo(respawnLocation);
    }

    @Test
    @DisplayName("handle throws EventHandlingException when game key of player does not return a game context")
    void handle_invalidPlayerGameKey() {
        Location respawnLocation = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy((() -> eventHandler.handle(event)))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerRespawnEvent for game key FREEPLAY, no corresponding game context was found");
    }

    @Test
    @DisplayName("handle sets respawn location to result from respawn handler if present")
    void handle_alterRespawnLocation() {
        GameContext gameContext = mock(GameContext.class);
        World world = mock(World.class);
        Location respawnLocation = new Location(world, 1, 1, 1);
        Location spawnPointLocation = new Location(world, 2, 2, 2);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        RespawnHandler respawnHandler = mock(RespawnHandler.class);
        when(respawnHandler.consumeRespawnLocation(PLAYER_ID)).thenReturn(Optional.of(spawnPointLocation));

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(respawnHandlerProvider.get()).thenReturn(respawnHandler);

        eventHandler.handle(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        assertThat(event.getRespawnLocation()).isEqualTo(spawnPointLocation);
    }
}
