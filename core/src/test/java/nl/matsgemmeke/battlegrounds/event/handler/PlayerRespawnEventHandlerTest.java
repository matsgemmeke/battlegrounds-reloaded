package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerRespawnEventHandlerTest {

    private GameContextProvider contextProvider;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
    }

    @Test
    public void handleDoesNotAlterEventIfPlayerIsNotInAnyContext() {
        Player player = mock(Player.class);
        Location respawnLocation = new Location(null, 1, 1, 1);

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        when(contextProvider.getGameKey(player)).thenReturn(null);

        PlayerRespawnEventHandler eventHandler = new PlayerRespawnEventHandler(contextProvider);
        eventHandler.handle(event);

        assertEquals(respawnLocation, event.getRespawnLocation());
    }

    @Test
    public void handleDoesNotAlterEventIfThereIsNoSpawnPointForThePlayer() {
        GameKey gameKey = GameKey.ofTrainingMode();
        Player player = mock(Player.class);
        Location respawnLocation = new Location(null, 1, 1, 1);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.findByEntity(player)).thenReturn(gamePlayer);

        SpawnPointProvider spawnPointProvider = mock(SpawnPointProvider.class);
        when(spawnPointProvider.hasSpawnPoint(gamePlayer)).thenReturn(false);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, PlayerRegistry.class)).thenReturn(playerRegistry);
        when(contextProvider.getComponent(gameKey, SpawnPointProvider.class)).thenReturn(spawnPointProvider);

        PlayerRespawnEventHandler eventHandler = new PlayerRespawnEventHandler(contextProvider);
        eventHandler.handle(event);

        assertEquals(respawnLocation, event.getRespawnLocation());
    }

    @Test
    public void handleSetsRespawnLocationIfPlayerHasSpawnPoint() {
        GameKey gameKey = GameKey.ofTrainingMode();
        Player player = mock(Player.class);
        Location respawnLocation = new Location(null, 1, 1, 1);
        Location spawnPointLocation = new Location(mock(World.class), 2, 2, 2);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        PlayerRespawnEvent event = new PlayerRespawnEvent(player, respawnLocation, false, false, RespawnReason.DEATH);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.findByEntity(player)).thenReturn(gamePlayer);

        SpawnPointProvider spawnPointProvider = mock(SpawnPointProvider.class);
        when(spawnPointProvider.hasSpawnPoint(gamePlayer)).thenReturn(true);
        when(spawnPointProvider.respawnEntity(gamePlayer)).thenReturn(spawnPointLocation);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, PlayerRegistry.class)).thenReturn(playerRegistry);
        when(contextProvider.getComponent(gameKey, SpawnPointProvider.class)).thenReturn(spawnPointProvider);

        PlayerRespawnEventHandler eventHandler = new PlayerRespawnEventHandler(contextProvider);
        eventHandler.handle(event);

        assertEquals(spawnPointLocation, event.getRespawnLocation());
    }
}
