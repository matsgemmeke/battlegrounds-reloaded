package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.access.provider.ActionHandlerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PlayerInteractEventHandlerTest {

    private ActionHandler actionHandler;
    private ActionHandlerProvider actionHandlerProvider;
    private Player player;

    @Before
    public void setUp() {
        actionHandler = mock(ActionHandler.class);
        actionHandlerProvider = mock(ActionHandlerProvider.class);
        player = mock(Player.class);
    }

    @Test
    public void shouldDoNothingIfClickedItemIsNull() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, null, null, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        verify(actionHandler, never()).handleItemLeftClick(any(), any());
        verify(actionHandler, never()).handleItemRightClick(any(), any());
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyGame() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(player, null, itemStack, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        verify(actionHandler, never()).handleItemLeftClick(any(), any());
        verify(actionHandler, never()).handleItemRightClick(any(), any());
    }

    @Test
    public void shouldCallLeftClickFunctionAndCancelEventBasedOnResult() {
        ItemStack itemStack = mock(ItemStack.class);

        when(actionHandler.handleItemLeftClick(player, itemStack)).thenReturn(false);
        when(actionHandlerProvider.getActionHandler(player)).thenReturn(actionHandler);

        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, itemStack, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        assertEquals(Result.DENY, event.useItemInHand());

        verify(actionHandler).handleItemLeftClick(player, itemStack);
    }

    @Test
    public void shouldCallRightClickFunctionAndCancelEventBasedOnResult() {
        ItemStack itemStack = mock(ItemStack.class);

        when(actionHandler.handleItemRightClick(player, itemStack)).thenReturn(true);
        when(actionHandlerProvider.getActionHandler(player)).thenReturn(actionHandler);

        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, itemStack, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(actionHandlerProvider);
        eventHandler.handle(event);

        assertEquals(Result.DEFAULT, event.useItemInHand());

        verify(actionHandler).handleItemRightClick(player, itemStack);
    }
}
