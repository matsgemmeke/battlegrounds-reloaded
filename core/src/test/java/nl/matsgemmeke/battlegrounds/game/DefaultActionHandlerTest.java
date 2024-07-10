package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultActionHandlerTest {

    private Game game;
    private ItemStack itemStack;
    private Player player;

    @Before
    public void setUp() {
        game = mock(Game.class);
        itemStack = new ItemStack(Material.IRON_HOE);
        player = mock(Player.class);
    }

    @Test
    public void shouldAllowItemChangeIfGameDoesNotHavePlayer() {
        when(game.getGamePlayer(player)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemChange(player, null, null);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnItemChangeFromToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack fromItem = new ItemStack(Material.IRON_HOE);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleChangeFromAction(gamePlayer, fromItem)).thenReturn(true);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleChangeFromAction(gamePlayer, fromItem)).thenReturn(true);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);
        boolean performAction = actionHandler.handleItemChange(player, fromItem, null);

        assertTrue(performAction);

        verify(behavior1).handleChangeFromAction(gamePlayer, fromItem);
        verify(behavior2).handleChangeFromAction(gamePlayer, fromItem);
    }

    @Test
    public void shouldPassItemChangeToToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack toItem = new ItemStack(Material.IRON_HOE);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleChangeToAction(gamePlayer, toItem)).thenReturn(true);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleChangeToAction(gamePlayer, toItem)).thenReturn(false);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);
        boolean performAction = actionHandler.handleItemChange(player, null, toItem);

        assertFalse(performAction);

        verify(behavior1).handleChangeToAction(gamePlayer, toItem);
        verify(behavior2).handleChangeToAction(gamePlayer, toItem);
    }

    @Test
    public void shouldAllowItemDropIfGameDoesNotHavePlayer() {
        when(game.getGamePlayer(player)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemDrop(player, itemStack);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnItemDropToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleDropItemAction(gamePlayer, itemStack)).thenReturn(true);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleDropItemAction(gamePlayer, itemStack)).thenReturn(true);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemDrop(player, itemStack);

        assertTrue(performAction);

        verify(behavior1).handleDropItemAction(gamePlayer, itemStack);
        verify(behavior2).handleDropItemAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowLeftClickIfGameDoesNotHavePlayer() {
        when(game.getGamePlayer(player)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemLeftClick(player, itemStack);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnLeftClickToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(false);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(true);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemLeftClick(player, itemStack);

        assertFalse(performAction);

        verify(behavior1).handleLeftClickAction(gamePlayer, itemStack);
        verify(behavior2).handleLeftClickAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowItemPickUpIfGameDoesNotHavePlayer() {
        when(game.getGamePlayer(player)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemPickup(player, itemStack);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnItemPickUpToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handlePickupItemAction(gamePlayer, itemStack)).thenReturn(false);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handlePickupItemAction(gamePlayer, itemStack)).thenReturn(false);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemPickup(player, itemStack);

        assertFalse(performAction);

        verify(behavior1).handlePickupItemAction(gamePlayer, itemStack);
        verify(behavior2).handlePickupItemAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowRightClickIfGameDoesNotHavePlayer() {
        when(game.getGamePlayer(player)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemRightClick(player, itemStack);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnRightClickToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(false);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(true);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemRightClick(player, itemStack);

        assertFalse(performAction);

        verify(behavior1).handleRightClickAction(gamePlayer, itemStack);
        verify(behavior2).handleRightClickAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowItemSwapIfGameDoesNotHavePlayer() {
        when(game.getGamePlayer(player)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean performAction = actionHandler.handleItemSwap(player, null, null);

        assertTrue(performAction);
    }

    @Test
    public void shouldPassOnItemSwapFromToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack fromItem = new ItemStack(Material.IRON_HOE);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleSwapFromAction(gamePlayer, fromItem)).thenReturn(true);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleSwapFromAction(gamePlayer, fromItem)).thenReturn(false);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);
        boolean performAction = actionHandler.handleItemSwap(player, fromItem, null);

        assertFalse(performAction);

        verify(behavior1).handleSwapFromAction(gamePlayer, fromItem);
        verify(behavior2).handleSwapFromAction(gamePlayer, fromItem);
    }

    @Test
    public void shouldPassItemSwapToToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack toItem = new ItemStack(Material.IRON_HOE);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleSwapToAction(gamePlayer, toItem)).thenReturn(true);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleSwapToAction(gamePlayer, toItem)).thenReturn(false);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);
        boolean performAction = actionHandler.handleItemSwap(player, null, toItem);

        assertFalse(performAction);

        verify(behavior1).handleSwapToAction(gamePlayer, toItem);
        verify(behavior2).handleSwapToAction(gamePlayer, toItem);
    }
}
