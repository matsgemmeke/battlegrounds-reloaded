package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.access.provider.ActionHandlerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerSwapHandItemsEventHandlerTest {

    private ActionHandlerProvider actionHandlerProvider;
    private Player player;

    @Before
    public void setUp() {
        actionHandlerProvider = mock(ActionHandlerProvider.class);
        player = mock(Player.class);
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyGame() {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, null, null);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        ItemStack swapFrom = new ItemStack(Material.IRON_HOE);
        ItemStack swapTo = new ItemStack(Material.IRON_HOE);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemSwap(player, swapFrom, swapTo)).thenReturn(false);

        when(actionHandlerProvider.getActionHandler(player)).thenReturn(actionHandler);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, swapTo, swapFrom);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemSwap(player, swapFrom, swapTo);
    }

    @Test
    public void shouldNotAlterCancelledEventIfActionHandlerDoesPerformTheAction() {
        ItemStack swapFrom = new ItemStack(Material.IRON_HOE);
        ItemStack swapTo = new ItemStack(Material.IRON_HOE);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemSwap(player, swapFrom, swapTo)).thenReturn(true);

        when(actionHandlerProvider.getActionHandler(player)).thenReturn(actionHandler);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, swapTo, swapFrom);
        event.setCancelled(true);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemSwap(player, swapFrom, swapTo);
    }
}
