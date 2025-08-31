package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.item.ActionExecutorRegistry;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final int PREVIOUS_SLOT = 0;
    private static final int CURRENT_SLOT = 1;
    private static final UUID PLAYER_ID = UUID.randomUUID();

    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Player player;
    private Provider<ActionExecutorRegistry> actionExecutorRegistryProvider;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        actionExecutorRegistryProvider = mock();

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGame() {
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameContextProvider, gameScope, actionExecutorRegistryProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenGameKeyOfPlayerHasNoCorrespondingGameContext() {
        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameContextProvider, gameScope, actionExecutorRegistryProvider);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process PlayerItemHeldEvent for game key OPEN-MODE, no corresponding game context was found");
    }

    @Test
    public void handleDoesNothingWhenItemStacksAreAir() {
        ItemStack changeFrom = new ItemStack(Material.AIR);
        ItemStack changeTo = new ItemStack(Material.AIR);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(inventory.getItem(CURRENT_SLOT)).thenReturn(changeTo);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(player.getInventory()).thenReturn(inventory);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameContextProvider, gameScope, actionExecutorRegistryProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    public void handleDoesNothingWhenNoCorrespondingActionExecutorsAreFound() {
        ItemStack changeFrom = this.createItemStack(Material.IRON_HOE);
        ItemStack changeTo = this.createItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(inventory.getItem(CURRENT_SLOT)).thenReturn(changeTo);

        ActionExecutorRegistry actionExecutorRegistry = mock(ActionExecutorRegistry.class);
        when(actionExecutorRegistry.getActionExecutor(changeFrom)).thenReturn(Optional.empty());
        when(actionExecutorRegistry.getActionExecutor(changeTo)).thenReturn(Optional.empty());

        when(actionExecutorRegistryProvider.get()).thenReturn(actionExecutorRegistry);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(player.getInventory()).thenReturn(inventory);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameContextProvider, gameScope, actionExecutorRegistryProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    public void handleCancelsEventBasedOnResultOfCorrespondingActionExecutors() {
        ItemStack changeFrom = this.createItemStack(Material.IRON_HOE);
        ItemStack changeTo = this.createItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(inventory.getItem(CURRENT_SLOT)).thenReturn(changeTo);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleChangeFromAction(player, changeFrom)).thenReturn(false);
        when(actionExecutor.handleChangeToAction(player, changeTo)).thenReturn(false);

        ActionExecutorRegistry actionExecutorRegistry = mock(ActionExecutorRegistry.class);
        when(actionExecutorRegistry.getActionExecutor(changeFrom)).thenReturn(Optional.of(actionExecutor));
        when(actionExecutorRegistry.getActionExecutor(changeTo)).thenReturn(Optional.of(actionExecutor));

        when(actionExecutorRegistryProvider.get()).thenReturn(actionExecutorRegistry);
        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(player.getInventory()).thenReturn(inventory);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, PREVIOUS_SLOT, CURRENT_SLOT);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameContextProvider, gameScope, actionExecutorRegistryProvider);
        eventHandler.handle(event);

        assertThat(event.isCancelled()).isTrue();
    }

    private ItemStack createItemStack(Material material) {
        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.getType()).thenReturn(material);

        return itemStack;
    }
}
