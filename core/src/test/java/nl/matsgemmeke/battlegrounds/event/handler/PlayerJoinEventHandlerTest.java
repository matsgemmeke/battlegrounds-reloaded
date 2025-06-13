package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class PlayerJoinEventHandlerTest {

    private GameContextProvider contextProvider;
    private PlayerLifecycleHandler playerLifecycleHandler;

    @BeforeEach
    public void setUp() {
        playerLifecycleHandler = mock(PlayerLifecycleHandler.class);

        contextProvider = mock(GameContextProvider.class);
        when(contextProvider.getComponent(GameKey.ofOpenMode(), PlayerLifecycleHandler.class)).thenReturn(playerLifecycleHandler);
    }

    @Test
    public void handleCallsPlayerLifeCycleHandlerToHandlePlayerJoin() {
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");

        PlayerJoinEventHandler eventHandler = new PlayerJoinEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(playerLifecycleHandler).handlePlayerJoin(player);
    }
}
