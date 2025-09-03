package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.event.action.ActionInvoker;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PlayerDropItemEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID PLAYER_ID = UUID.randomUUID();

    private ActionInvoker actionInvoker;
    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Item item;

    @BeforeEach
    public void setUp() {
        actionInvoker = mock(ActionInvoker.class);
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        item = mock(Item.class);
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGame() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(actionInvoker, gameContextProvider, gameScope);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenUnableToFindGameContextForPlayerGameKey() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(actionInvoker, gameContextProvider, gameScope);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerDropItemEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    public void handleInvokesActionExecutorAndCancelsEventAccordingToActionResult() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleDropItemAction(player, ITEM_STACK)).thenReturn(false);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(item.getItemStack()).thenReturn(ITEM_STACK);

        doAnswer(invocation -> {
            Function<ActionExecutor, Boolean> function = invocation.getArgument(1);
            return function.apply(actionExecutor);
        }).when(actionInvoker).performAction(eq(ITEM_STACK), any());

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(actionInvoker, gameContextProvider, gameScope);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isTrue();

        verify(actionExecutor).handleDropItemAction(player, ITEM_STACK);
    }

    @Test
    public void handleInvokesActionExecutorAndDoesNotCancelEventAccordingToActionResult() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleDropItemAction(player, ITEM_STACK)).thenReturn(true);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(item.getItemStack()).thenReturn(ITEM_STACK);

        doAnswer(invocation -> {
            Function<ActionExecutor, Boolean> function = invocation.getArgument(1);
            return function.apply(actionExecutor);
        }).when(actionInvoker).performAction(eq(ITEM_STACK), any());

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(actionInvoker, gameContextProvider, gameScope);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();

        verify(actionExecutor).handleDropItemAction(player, ITEM_STACK);
    }
}
