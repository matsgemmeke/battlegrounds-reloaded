package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PlayerJoinEventHandlerTest {

    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        playerLifecycleHandlerProvider = mock();
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenNoGameContextExistsForOpenMode() {
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");

        when(gameContextProvider.getGameContext(GameKey.ofOpenMode())).thenReturn(Optional.empty());

        PlayerJoinEventHandler eventHandler = new PlayerJoinEventHandler(gameContextProvider, gameScope, playerLifecycleHandlerProvider);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerJoinEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    public void handleCallsPlayerLifeCycleHandlerToHandlePlayerJoin() {
        GameContext gameContext = mock(GameContext.class);
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");
        PlayerLifecycleHandler playerLifecycleHandler = mock(PlayerLifecycleHandler.class);

        when(gameContextProvider.getGameContext(GameKey.ofOpenMode())).thenReturn(Optional.of(gameContext));
        when(playerLifecycleHandlerProvider.get()).thenReturn(playerLifecycleHandler);

        PlayerJoinEventHandler eventHandler = new PlayerJoinEventHandler(gameContextProvider, gameScope, playerLifecycleHandlerProvider);
        eventHandler.handle(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        verify(playerLifecycleHandler).handlePlayerJoin(player);
    }
}
