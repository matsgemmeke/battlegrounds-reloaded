package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EntityPickupItemEventHandlerTest {

    private GameContextProvider contextProvider;
    private Item item;

    @BeforeEach
    public void setUp() {
        this.contextProvider = mock(GameContextProvider.class);
        this.item = mock(Item.class);
    }

    @Test
    public void handleShouldDoNothingIfEntityIsNoPlayer() {
        Zombie zombie = mock(Zombie.class);

        EntityPickupItemEvent event = new EntityPickupItemEvent(zombie, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGame() {
        Player player = mock(Player.class);

        when(contextProvider.getGameKey(player)).thenReturn(null);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void handleShouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        GameKey gameKey = GameKey.ofTrainingMode();
        Player player = mock(Player.class);

        ItemStack itemStack = mock(ItemStack.class);
        when(item.getItemStack()).thenReturn(itemStack);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemPickup(player, itemStack)).thenReturn(false);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, ActionHandler.class)).thenReturn(actionHandler);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemPickup(player, itemStack);
    }

    @Test
    public void handleShouldNotAlterCancelledEventIfActionHandlerDoesPerformTheAction() {
        GameKey gameKey = GameKey.ofTrainingMode();
        Player player = mock(Player.class);

        ItemStack itemStack = mock(ItemStack.class);
        when(item.getItemStack()).thenReturn(itemStack);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemPickup(player, itemStack)).thenReturn(true);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, ActionHandler.class)).thenReturn(actionHandler);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);
        event.setCancelled(true);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemPickup(player, itemStack);
    }
}
