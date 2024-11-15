package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PlayerInteractEventHandlerTest {

    private GameContextProvider contextProvider;
    private Player player;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        player = mock(Player.class);
    }

    @Test
    public void shouldDoNothingIfClickedItemIsNull() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, null, null, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(contextProvider);
        eventHandler.handle(event);

        assertEquals(Result.DEFAULT, event.useItemInHand());
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyContext() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(player, null, itemStack, null, null);

        when(contextProvider.getContext(player)).thenReturn(null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(contextProvider);
        eventHandler.handle(event);

        assertEquals(Result.DEFAULT, event.useItemInHand());
    }

    @Test
    public void shouldCallLeftClickFunctionAndCancelEventBasedOnResult() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, itemStack, null, null);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemLeftClick(player, itemStack)).thenReturn(false);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(contextProvider);
        eventHandler.handle(event);

        assertEquals(Result.DENY, event.useItemInHand());

        verify(actionHandler).handleItemLeftClick(player, itemStack);
    }

    @Test
    public void shouldCallRightClickFunctionAndCancelEventBasedOnResult() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, itemStack, null, null);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemRightClick(player, itemStack)).thenReturn(true);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(contextProvider);
        eventHandler.handle(event);

        assertEquals(Result.DEFAULT, event.useItemInHand());

        verify(actionHandler).handleItemRightClick(player, itemStack);
    }
}
