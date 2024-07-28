package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerDropItemEventHandlerTest {

    private GameContextProvider contextProvider;
    private Item item;
    private Player player;
    private PlayerDropItemEvent event;

    @Before
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        item = mock(Item.class);
        player = mock(Player.class);
        event = new PlayerDropItemEvent(player, item);
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyGames() {
        when(contextProvider.getContext(player)).thenReturn(null);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        when(item.getItemStack()).thenReturn(itemStack);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemDrop(player, itemStack)).thenReturn(false);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemDrop(player, itemStack);
    }

    @Test
    public void shouldNotAlterCancelledEventIfActionHandlerDoesPerformTheAction() {
        event.setCancelled(true);

        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        when(item.getItemStack()).thenReturn(itemStack);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemDrop(player, itemStack)).thenReturn(true);

        GameContext context = mock(GameContext.class);
        when(context.getActionHandler()).thenReturn(actionHandler);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemDrop(player, itemStack);
    }
}
