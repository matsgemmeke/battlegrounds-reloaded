package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PlayerQuitEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID PLAYER_ID = UUID.randomUUID();

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
    public void handleDoesNothingWhenPlayerIsNotInAnyGameContext() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerQuitEvent event = new PlayerQuitEvent(player, "test");

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        PlayerQuitEventHandler eventHandler = new PlayerQuitEventHandler(gameContextProvider, gameScope, playerLifecycleHandlerProvider);
        eventHandler.handle(event);

        verifyNoInteractions(gameScope);
        verifyNoInteractions(playerLifecycleHandlerProvider);
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenNoGameContextExistsForGameKeyOfPlayer() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerQuitEvent event = new PlayerQuitEvent(player, "test");

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        PlayerQuitEventHandler eventHandler = new PlayerQuitEventHandler(gameContextProvider, gameScope, playerLifecycleHandlerProvider);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerKickEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    public void handlePerformsDeregisterWhenPlayerIsInGame() {
        GameContext gameContext = mock(GameContext.class);
        PlayerLifecycleHandler playerLifecycleHandler = mock(PlayerLifecycleHandler.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerQuitEvent event = new PlayerQuitEvent(player, "test");

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(playerLifecycleHandlerProvider.get()).thenReturn(playerLifecycleHandler);

        PlayerQuitEventHandler eventHandler = new PlayerQuitEventHandler(gameContextProvider, gameScope, playerLifecycleHandlerProvider);
        eventHandler.handle(event);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        verify(playerLifecycleHandler).handlePlayerLeave(PLAYER_ID);
    }
}
