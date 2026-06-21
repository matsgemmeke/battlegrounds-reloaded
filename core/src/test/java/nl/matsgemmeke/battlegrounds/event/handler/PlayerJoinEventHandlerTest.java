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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerJoinEventHandlerTest {

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider;
    @InjectMocks
    private PlayerJoinEventHandler eventHandler;

    @Test
    @DisplayName("handle throws EventHandlingException when no game context exists for freeplay")
    void handle_withoutFreeplayGameContext() {
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");

        when(gameContextProvider.getGameContext(GameKey.ofFreeplay())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerJoinEvent for game key FREEPLAY, no corresponding game context was found");
    }

    @Test
    @DisplayName("handle calls PlayerLifecycleHandler to handle player join")
    void handle_successful() {
        GameContext gameContext = mock(GameContext.class);
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");
        PlayerLifecycleHandler playerLifecycleHandler = mock(PlayerLifecycleHandler.class);

        when(gameContextProvider.getGameContext(GameKey.ofFreeplay())).thenReturn(Optional.of(gameContext));
        when(playerLifecycleHandlerProvider.get()).thenReturn(playerLifecycleHandler);

        eventHandler.handle(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        verify(playerLifecycleHandler).handlePlayerJoin(player);
    }
}
