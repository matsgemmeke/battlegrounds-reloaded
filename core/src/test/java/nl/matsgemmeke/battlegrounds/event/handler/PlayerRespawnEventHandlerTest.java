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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PlayerRespawnEventHandlerTest {

    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Provider<RespawnHandler> respawnHandlerProvider;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        respawnHandlerProvider = mock();
    }

    @Test
    public void handleDoesNotAlterEventIfPlayerIsNotInAnyGameContext() {
        UUID playerId = UUID.randomUUID();
        Location respawnLocation = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        when(gameContextProvider.getGameKeyByEntityId(playerId)).thenReturn(Optional.empty());

        PlayerRespawnEventHandler eventHandler = new PlayerRespawnEventHandler(gameContextProvider, gameScope, respawnHandlerProvider);
        eventHandler.handle(event);

        assertThat(event.getRespawnLocation()).isEqualTo(respawnLocation);
    }

    @Test
    public void handleThrowsIllegalStateExceptionWhenGameKeyFromPlayerDoesNotReturnGameContext() {
        GameKey gameKey = GameKey.ofOpenMode();
        Location respawnLocation = new Location(null, 1, 1, 1);
        UUID playerId = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        when(gameContextProvider.getGameKeyByEntityId(playerId)).thenReturn(Optional.of(gameKey));
        when(gameContextProvider.getGameContext(gameKey)).thenReturn(Optional.empty());

        PlayerRespawnEventHandler eventHandler = new PlayerRespawnEventHandler(gameContextProvider, gameScope, respawnHandlerProvider);

        assertThatThrownBy((() -> eventHandler.handle(event)))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerRespawnEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    public void handleSetsRespawnLocationToResultFromRespawnHandlerIfPresent() {
        GameKey gameKey = GameKey.ofOpenMode();
        GameContext gameContext = mock(GameContext.class);
        World world = mock(World.class);
        Location respawnLocation = new Location(world, 1, 1, 1);
        Location spawnPointLocation = new Location(world, 2, 2, 2);
        UUID playerId = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);

        RespawnHandler respawnHandler = mock(RespawnHandler.class);
        when(respawnHandler.consumeRespawnLocation(playerId)).thenReturn(Optional.of(spawnPointLocation));

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        when(gameContextProvider.getGameKeyByEntityId(playerId)).thenReturn(Optional.of(gameKey));
        when(gameContextProvider.getGameContext(gameKey)).thenReturn(Optional.of(gameContext));
        when(respawnHandlerProvider.get()).thenReturn(respawnHandler);

        PlayerRespawnEventHandler eventHandler = new PlayerRespawnEventHandler(gameContextProvider, gameScope, respawnHandlerProvider);
        eventHandler.handle(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        assertThat(event.getRespawnLocation()).isEqualTo(spawnPointLocation);
    }
}
