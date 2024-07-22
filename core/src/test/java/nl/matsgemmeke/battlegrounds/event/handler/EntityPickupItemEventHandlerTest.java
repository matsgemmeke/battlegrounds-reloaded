package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EntityPickupItemEventHandlerTest {

    private GameContextProvider contextProvider;
    private Item item;

    @Before
    public void setUp() {
        this.contextProvider = mock(GameContextProvider.class);
        this.item = mock(Item.class);
    }

    @Test
    public void shouldDoNothingIfEntityIsNotPlayer() {
        Zombie zombie = mock(Zombie.class);

        EntityPickupItemEvent event = new EntityPickupItemEvent(zombie, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyGame() {
        Player player = mock(Player.class);

        when(contextProvider.getContext(player)).thenReturn(null);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        Player player = mock(Player.class);

        ItemStack itemStack = mock(ItemStack.class);
        when(item.getItemStack()).thenReturn(itemStack);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemPickup(player, itemStack)).thenReturn(false);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemPickup(player, itemStack);
    }

    @Test
    public void shouldNotAlterCancelledEventIfActionHandlerDoesPerformTheAction() {
        Player player = mock(Player.class);

        ItemStack itemStack = mock(ItemStack.class);
        when(item.getItemStack()).thenReturn(itemStack);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemPickup(player, itemStack)).thenReturn(true);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);
        event.setCancelled(true);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemPickup(player, itemStack);
    }
}
