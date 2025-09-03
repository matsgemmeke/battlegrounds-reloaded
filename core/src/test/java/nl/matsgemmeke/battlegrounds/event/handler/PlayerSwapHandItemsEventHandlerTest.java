package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.component.item.ActionInvoker;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PlayerSwapHandItemsEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final ItemStack MAIN_HAND_ITEM = createItemStack(Material.IRON_HOE);
    private static final ItemStack OFF_HAND_ITEM = createItemStack(Material.IRON_HOE);
    private static final UUID PLAYER_ID = UUID.randomUUID();

    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Player player;
    private Provider<ActionInvoker> actionInvokerProvider;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        actionInvokerProvider = mock();

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGame() {
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, MAIN_HAND_ITEM, OFF_HAND_ITEM);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(gameContextProvider, gameScope, actionInvokerProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenGameKeyOfPlayerHasNoCorrespondingGameContext() {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, MAIN_HAND_ITEM, OFF_HAND_ITEM);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(gameContextProvider, gameScope, actionInvokerProvider);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerSwapHandItemsEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleCancelsEventBasedOnResultOfCorrespondingActionInvoker() {
        ActionInvoker actionInvoker = mock(ActionInvoker.class);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleSwapFromAction(player, OFF_HAND_ITEM)).thenReturn(false);
        when(actionExecutor.handleSwapToAction(player, OFF_HAND_ITEM)).thenReturn(false);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(actionInvokerProvider.get()).thenReturn(actionInvoker);

        doAnswer(invocation -> {
            ((Function<ActionExecutor, Boolean>) invocation.getArgument(1)).apply(actionExecutor);
            return null;
        }).when(actionInvoker).performAction(eq(MAIN_HAND_ITEM), any());
        doAnswer(invocation -> {
            ((Function<ActionExecutor, Boolean>) invocation.getArgument(1)).apply(actionExecutor);
            return null;
        }).when(actionInvoker).performAction(eq(OFF_HAND_ITEM), any());

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, MAIN_HAND_ITEM, OFF_HAND_ITEM);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(gameContextProvider, gameScope, actionInvokerProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isTrue();

        verify(actionExecutor).handleSwapFromAction(player, OFF_HAND_ITEM);
        verify(actionExecutor).handleSwapToAction(player, MAIN_HAND_ITEM);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleInvokesActionExecutorButDoesNotCancelEventWhenAlreadyCancelled() {
        ActionInvoker actionInvoker = mock(ActionInvoker.class);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleSwapFromAction(player, OFF_HAND_ITEM)).thenReturn(true);
        when(actionExecutor.handleSwapToAction(player, MAIN_HAND_ITEM)).thenReturn(true);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(actionInvokerProvider.get()).thenReturn(actionInvoker);

        doAnswer(invocation -> {
            ((Function<ActionExecutor, Boolean>) invocation.getArgument(1)).apply(actionExecutor);
            return null;
        }).when(actionInvoker).performAction(eq(MAIN_HAND_ITEM), any());
        doAnswer(invocation -> {
            ((Function<ActionExecutor, Boolean>) invocation.getArgument(1)).apply(actionExecutor);
            return null;
        }).when(actionInvoker).performAction(eq(OFF_HAND_ITEM), any());

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, MAIN_HAND_ITEM, OFF_HAND_ITEM);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(gameContextProvider, gameScope, actionInvokerProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isTrue();

        verify(actionExecutor).handleSwapFromAction(player, OFF_HAND_ITEM);
        verify(actionExecutor).handleSwapToAction(player, MAIN_HAND_ITEM);
    }

    private static ItemStack createItemStack(Material material) {
        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.getType()).thenReturn(material);

        return itemStack;
    }
}
