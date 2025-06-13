package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerDropItemEventHandlerTest {

    private GameContextProvider contextProvider;
    private Item item;
    private Player player;
    private PlayerDropItemEvent event;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        item = mock(Item.class);
        player = mock(Player.class);
        event = new PlayerDropItemEvent(player, item);
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGames() {
        when(contextProvider.getGameKey(player)).thenReturn(null);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        GameKey gameKey = GameKey.ofOpenMode();

        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        when(item.getItemStack()).thenReturn(itemStack);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemDrop(player, itemStack)).thenReturn(false);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, ActionHandler.class)).thenReturn(actionHandler);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemDrop(player, itemStack);
    }

    @Test
    public void shouldNotAlterCancelledEventIfActionHandlerDoesPerformTheAction() {
        GameKey gameKey = GameKey.ofOpenMode();

        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        when(item.getItemStack()).thenReturn(itemStack);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemDrop(player, itemStack)).thenReturn(true);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, ActionHandler.class)).thenReturn(actionHandler);

        event.setCancelled(true);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemDrop(player, itemStack);
    }
}
