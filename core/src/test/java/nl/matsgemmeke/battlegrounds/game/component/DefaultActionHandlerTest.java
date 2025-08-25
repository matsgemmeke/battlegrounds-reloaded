package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultActionHandlerTest {

    private Game game;
    private ItemStack itemStack;
    private Player player;
    private PlayerRegistry playerRegistry;
    private UUID uuid;

    @BeforeEach
    public void setUp() {
        game = mock(Game.class);
        itemStack = new ItemStack(Material.IRON_HOE);
        playerRegistry = mock(PlayerRegistry.class);
        uuid = UUID.randomUUID();

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);
    }

    @Test
    public void shouldAllowItemChangeIfGameDoesNotHavePlayer() {
        when(playerRegistry.findByUUID(uuid)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemChange(player, null, null);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnItemChangeFromToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack fromItem = new ItemStack(Material.IRON_HOE);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleChangeFromAction(gamePlayer, fromItem)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleChangeFromAction(gamePlayer, fromItem)).thenReturn(true);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);
        boolean performAction = actionHandler.handleItemChange(player, fromItem, null);

        assertTrue(performAction);

        verify(actionExecutor1).handleChangeFromAction(gamePlayer, fromItem);
        verify(actionExecutor2).handleChangeFromAction(gamePlayer, fromItem);
    }

    @Test
    public void shouldPassItemChangeToToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack toItem = new ItemStack(Material.IRON_HOE);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleChangeToAction(gamePlayer, toItem)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleChangeToAction(gamePlayer, toItem)).thenReturn(false);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);
        boolean performAction = actionHandler.handleItemChange(player, null, toItem);

        assertFalse(performAction);

        verify(actionExecutor1).handleChangeToAction(gamePlayer, toItem);
        verify(actionExecutor2).handleChangeToAction(gamePlayer, toItem);
    }

    @Test
    public void shouldAllowItemDropIfGameDoesNotHavePlayer() {
        when(playerRegistry.findByUUID(uuid)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemDrop(player, itemStack);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnItemDropToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleDropItemAction(gamePlayer, itemStack)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleDropItemAction(gamePlayer, itemStack)).thenReturn(true);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemDrop(player, itemStack);

        assertTrue(performAction);

        verify(actionExecutor1).handleDropItemAction(gamePlayer, itemStack);
        verify(actionExecutor2).handleDropItemAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowLeftClickIfGameDoesNotHavePlayer() {
        when(playerRegistry.findByUUID(uuid)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemLeftClick(player, itemStack);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnLeftClickToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(false);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(true);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemLeftClick(player, itemStack);

        assertFalse(performAction);

        verify(actionExecutor1).handleLeftClickAction(gamePlayer, itemStack);
        verify(actionExecutor2).handleLeftClickAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowItemPickUpIfGameDoesNotHavePlayer() {
        when(playerRegistry.findByUUID(uuid)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemPickup(player, itemStack);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnItemPickUpToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handlePickupItemAction(gamePlayer, itemStack)).thenReturn(false);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handlePickupItemAction(gamePlayer, itemStack)).thenReturn(false);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemPickup(player, itemStack);

        assertFalse(performAction);

        verify(actionExecutor1).handlePickupItemAction(gamePlayer, itemStack);
        verify(actionExecutor2).handlePickupItemAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowRightClickIfGameDoesNotHavePlayer() {
        when(playerRegistry.findByUUID(uuid)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemRightClick(player, itemStack);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnRightClickToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(false);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(true);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemRightClick(player, itemStack);

        assertFalse(performAction);

        verify(actionExecutor1).handleRightClickAction(gamePlayer, itemStack);
        verify(actionExecutor2).handleRightClickAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowItemSwapIfGameDoesNotHavePlayer() {
        when(playerRegistry.findByUUID(uuid)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemSwap(player, null, null);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnItemSwapFromToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack fromItem = new ItemStack(Material.IRON_HOE);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleSwapFromAction(gamePlayer, fromItem)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleSwapFromAction(gamePlayer, fromItem)).thenReturn(false);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);
        boolean performAction = actionHandler.handleItemSwap(player, fromItem, null);

        assertFalse(performAction);

        verify(actionExecutor1).handleSwapFromAction(gamePlayer, fromItem);
        verify(actionExecutor2).handleSwapFromAction(gamePlayer, fromItem);
    }

    @Test
    public void shouldPassItemSwapToToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack toItem = new ItemStack(Material.IRON_HOE);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleSwapToAction(gamePlayer, toItem)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleSwapToAction(gamePlayer, toItem)).thenReturn(false);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);
        boolean performAction = actionHandler.handleItemSwap(player, null, toItem);

        assertFalse(performAction);

        verify(actionExecutor1).handleSwapToAction(gamePlayer, toItem);
        verify(actionExecutor2).handleSwapToAction(gamePlayer, toItem);
    }
}
