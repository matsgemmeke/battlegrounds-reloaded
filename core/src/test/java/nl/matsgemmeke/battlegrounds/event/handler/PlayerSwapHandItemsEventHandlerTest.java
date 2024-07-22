package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
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

    private GameContextProvider contextProvider;
    private Player player;

    @Before
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        player = mock(Player.class);
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyContext() {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, null, null);

        when(contextProvider.getContext(player)).thenReturn(null);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        ItemStack swapFrom = new ItemStack(Material.IRON_HOE);
        ItemStack swapTo = new ItemStack(Material.IRON_HOE);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemSwap(player, swapFrom, swapTo)).thenReturn(false);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, swapTo, swapFrom);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(contextProvider);
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

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, swapTo, swapFrom);
        event.setCancelled(true);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemSwap(player, swapFrom, swapTo);
    }
}
