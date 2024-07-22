package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

    private GameContextProvider contextProvider;
    private Player player;

    @Before
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        player = mock(Player.class);
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyGame() {
        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        ItemStack changeFrom = new ItemStack(Material.IRON_HOE);
        ItemStack changeTo = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItem(1)).thenReturn(changeTo);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(player.getInventory()).thenReturn(inventory);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemChange(player, changeFrom, changeTo)).thenReturn(false);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
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

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemChange(player, changeFrom, changeTo)).thenReturn(true);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);
        event.setCancelled(true);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemChange(player, changeFrom, changeTo);
    }
}
