package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerQuitEventHandlerTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Provider<PlayerLifecycleHandler> playerLifecycleHandlerProvider;
    @InjectMocks
    private PlayerQuitEventHandler eventHandler;

    @Test
    @DisplayName("handle does nothing when player is not in any game context")
    void handle_notInGameContext() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerQuitEvent event = new PlayerQuitEvent(player, "test");

        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.empty());

        eventHandler.handle(event);

        verifyNoInteractions(gameScope);
        verifyNoInteractions(playerLifecycleHandlerProvider);
    }

    @Test
    @DisplayName("handle performs deregister when player is in a game context")
    void handle_successful() {
        GameContext gameContext = mock(GameContext.class);
        PlayerLifecycleHandler playerLifecycleHandler = mock(PlayerLifecycleHandler.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerQuitEvent event = new PlayerQuitEvent(player, "test");

        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.of(gameContext));
        when(playerLifecycleHandlerProvider.get()).thenReturn(playerLifecycleHandler);
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(gameContext), any(Runnable.class));

        eventHandler.handle(event);

        verify(playerLifecycleHandler).handlePlayerLeave(PLAYER_ID);
    }
}
