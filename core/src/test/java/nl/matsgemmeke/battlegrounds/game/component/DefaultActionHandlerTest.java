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
        when(actionExecutor1.handleChangeFromAction(player, fromItem)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleChangeFromAction(player, fromItem)).thenReturn(true);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);
        boolean performAction = actionHandler.handleItemChange(player, fromItem, null);

        assertTrue(performAction);

        verify(actionExecutor1).handleChangeFromAction(player, fromItem);
        verify(actionExecutor2).handleChangeFromAction(player, fromItem);
    }

    @Test
    public void shouldPassItemChangeToToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack toItem = new ItemStack(Material.IRON_HOE);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleChangeToAction(player, toItem)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleChangeToAction(player, toItem)).thenReturn(false);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);
        boolean performAction = actionHandler.handleItemChange(player, null, toItem);

        assertFalse(performAction);

        verify(actionExecutor1).handleChangeToAction(player, toItem);
        verify(actionExecutor2).handleChangeToAction(player, toItem);
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
        when(actionExecutor1.handleDropItemAction(player, itemStack)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleDropItemAction(player, itemStack)).thenReturn(true);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemDrop(player, itemStack);

        assertTrue(performAction);

        verify(actionExecutor1).handleDropItemAction(player, itemStack);
        verify(actionExecutor2).handleDropItemAction(player, itemStack);
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
        when(actionExecutor1.handleLeftClickAction(player, itemStack)).thenReturn(false);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleLeftClickAction(player, itemStack)).thenReturn(true);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemLeftClick(player, itemStack);

        assertFalse(performAction);

        verify(actionExecutor1).handleLeftClickAction(player, itemStack);
        verify(actionExecutor2).handleLeftClickAction(player, itemStack);
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
        when(actionExecutor1.handlePickupItemAction(player, itemStack)).thenReturn(false);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handlePickupItemAction(player, itemStack)).thenReturn(false);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemPickup(player, itemStack);

        assertFalse(performAction);

        verify(actionExecutor1).handlePickupItemAction(player, itemStack);
        verify(actionExecutor2).handlePickupItemAction(player, itemStack);
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
        when(actionExecutor1.handleLeftClickAction(player, itemStack)).thenReturn(false);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleLeftClickAction(player, itemStack)).thenReturn(true);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);

        boolean performAction = actionHandler.handleItemRightClick(player, itemStack);

        assertFalse(performAction);

        verify(actionExecutor1).handleRightClickAction(player, itemStack);
        verify(actionExecutor2).handleRightClickAction(player, itemStack);
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
        when(actionExecutor1.handleSwapFromAction(player, fromItem)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleSwapFromAction(player, fromItem)).thenReturn(false);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);
        boolean performAction = actionHandler.handleItemSwap(player, fromItem, null);

        assertFalse(performAction);

        verify(actionExecutor1).handleSwapFromAction(player, fromItem);
        verify(actionExecutor2).handleSwapFromAction(player, fromItem);
    }

    @Test
    public void shouldPassItemSwapToToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack toItem = new ItemStack(Material.IRON_HOE);

        ActionExecutor actionExecutor1 = mock(ActionExecutor.class);
        when(actionExecutor1.handleSwapToAction(player, toItem)).thenReturn(true);

        ActionExecutor actionExecutor2 = mock(ActionExecutor.class);
        when(actionExecutor2.handleSwapToAction(player, toItem)).thenReturn(false);

        when(game.getActionExecutors()).thenReturn(List.of(actionExecutor1, actionExecutor2));
        when(playerRegistry.findByUUID(uuid)).thenReturn(gamePlayer);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game, playerRegistry);
        boolean performAction = actionHandler.handleItemSwap(player, null, toItem);

        assertFalse(performAction);

        verify(actionExecutor1).handleSwapToAction(player, toItem);
        verify(actionExecutor2).handleSwapToAction(player, toItem);
    }
}
