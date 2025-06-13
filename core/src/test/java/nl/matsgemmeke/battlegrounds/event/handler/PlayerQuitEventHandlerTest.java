package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class PlayerQuitEventHandlerTest {

    private GameContextProvider contextProvider;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
    }

    @Test
    public void handleDoesNotPerformDeregisterWhenPlayerIsNotInAnyGame() {
        Player player = mock(Player.class);
        PlayerQuitEvent event = new PlayerQuitEvent(player, null);

        when(contextProvider.getGameKey(player)).thenReturn(null);

        PlayerQuitEventHandler eventHandler = new PlayerQuitEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(contextProvider, never()).getComponent(any(GameKey.class), any());
    }

    @Test
    public void handlePerformsDeregisterWhenPlayerIsInGame() {
        GameKey gameKey = GameKey.ofOpenMode();
        PlayerLifecycleHandler playerLifecycleHandler = mock(PlayerLifecycleHandler.class);
        UUID playerUuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerUuid);

        PlayerQuitEvent event = new PlayerQuitEvent(player, null);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, PlayerLifecycleHandler.class)).thenReturn(playerLifecycleHandler);

        PlayerQuitEventHandler eventHandler = new PlayerQuitEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(playerLifecycleHandler).handlePlayerLeave(playerUuid);
    }
}
