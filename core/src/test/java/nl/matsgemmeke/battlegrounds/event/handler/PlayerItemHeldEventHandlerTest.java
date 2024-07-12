package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.access.provider.ActionHandlerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

    private ActionHandler actionHandler;
    private ActionHandlerProvider actionHandlerProvider;
    private Player player;

    @Before
    public void setUp() {
        this.actionHandler = mock(ActionHandler.class);
        this.actionHandlerProvider = mock(ActionHandlerProvider.class);
        this.player = mock(Player.class);
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyGame() {
        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        verifyNoInteractions(actionHandler);
    }

    @Test
    public void shouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        ItemStack changeFrom = new ItemStack(Material.IRON_HOE);
        ItemStack changeTo = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItem(1)).thenReturn(changeTo);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(player.getInventory()).thenReturn(inventory);

        when(actionHandler.handleItemChange(player, changeFrom, changeTo)).thenReturn(false);

        when(actionHandlerProvider.getActionHandler(player)).thenReturn(actionHandler);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemChange(player, changeFrom, changeTo);
    }

    @Test
    public void shouldNotAlterCancelledEventIfActionHandlerDoesPerformTheAction() {
        ItemStack changeFrom = new ItemStack(Material.IRON_HOE);
        ItemStack changeTo = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItem(1)).thenReturn(changeTo);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(player.getInventory()).thenReturn(inventory);

        when(actionHandler.handleItemChange(player, changeFrom, changeTo)).thenReturn(true);

        when(actionHandlerProvider.getActionHandler(player)).thenReturn(actionHandler);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);
        event.setCancelled(true);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemChange(player, changeFrom, changeTo);
    }
}
