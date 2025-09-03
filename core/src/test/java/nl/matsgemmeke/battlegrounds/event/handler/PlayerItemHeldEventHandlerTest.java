package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.event.action.ActionInvoker;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final int PREVIOUS_SLOT = 0;
    private static final int CURRENT_SLOT = 1;
    private static final UUID PLAYER_ID = UUID.randomUUID();

    private ActionInvoker actionInvoker;
    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Player player;

    @BeforeEach
    public void setUp() {
        actionInvoker = mock(ActionInvoker.class);
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGame() {
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(actionInvoker, gameContextProvider, gameScope);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenGameKeyOfPlayerHasNoCorrespondingGameContext() {
        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(actionInvoker, gameContextProvider, gameScope);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerItemHeldEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    public void handleCancelsEventBasedOnResultOfActionInvoker() {
        ItemStack changeFrom = this.createItemStack(Material.IRON_HOE);
        ItemStack changeTo = this.createItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(inventory.getItem(CURRENT_SLOT)).thenReturn(changeTo);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleChangeFromAction(player, changeFrom)).thenReturn(true);
        when(actionExecutor.handleChangeToAction(player, changeTo)).thenReturn(true);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(player.getInventory()).thenReturn(inventory);

        doAnswer(invocation -> {
            Function<ActionExecutor, Boolean> function = invocation.getArgument(1);
            return function.apply(actionExecutor);
        }).when(actionInvoker).performAction(any(ItemStack.class), any());

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(actionInvoker, gameContextProvider, gameScope);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();

        verify(actionExecutor).handleChangeFromAction(player, changeFrom);
        verify(actionExecutor).handleChangeToAction(player, changeTo);
    }

    @Test
    public void handleInvokesActionExecutorButDoesNotCancelEventWhenAlreadyCancelled() {
        ItemStack changeFrom = this.createItemStack(Material.IRON_HOE);
        ItemStack changeTo = this.createItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(inventory.getItem(CURRENT_SLOT)).thenReturn(changeTo);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleChangeFromAction(player, changeFrom)).thenReturn(false);
        when(actionExecutor.handleChangeToAction(player, changeTo)).thenReturn(false);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(player.getInventory()).thenReturn(inventory);

        doAnswer(invocation -> {
            Function<ActionExecutor, Boolean> function = invocation.getArgument(1);
            return function.apply(actionExecutor);
        }).when(actionInvoker).performAction(any(ItemStack.class), any());

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);
        event.setCancelled(true);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(actionInvoker, gameContextProvider, gameScope);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isTrue();

        verify(actionExecutor).handleChangeFromAction(player, changeFrom);
        verify(actionExecutor).handleChangeToAction(player, changeTo);
    }

    private ItemStack createItemStack(Material material) {
        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.getType()).thenReturn(material);

        return itemStack;
    }
}
