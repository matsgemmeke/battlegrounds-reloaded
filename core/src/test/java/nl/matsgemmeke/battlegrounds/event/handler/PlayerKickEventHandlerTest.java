package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class PlayerKickEventHandlerTest {

    private GameContextProvider contextProvider;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
    }

    @Test
    public void handleDoesNotPerformDeregisterWhenPlayerIsNotInAnyGame() {
        Player player = mock(Player.class);
        PlayerKickEvent event = new PlayerKickEvent(player, "test", "test");

        when(contextProvider.getGameKey(player)).thenReturn(null);

        PlayerKickEventHandler eventHandler = new PlayerKickEventHandler(contextProvider);
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

        PlayerKickEvent event = new PlayerKickEvent(player, "test", "test");

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, PlayerLifecycleHandler.class)).thenReturn(playerLifecycleHandler);

        PlayerKickEventHandler eventHandler = new PlayerKickEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(playerLifecycleHandler).handlePlayerLeave(playerUuid);
    }
}
