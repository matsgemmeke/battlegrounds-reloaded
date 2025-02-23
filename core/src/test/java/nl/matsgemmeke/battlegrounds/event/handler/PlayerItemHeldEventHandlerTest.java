package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

    private GameContextProvider contextProvider;
    private Player player;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        player = mock(Player.class);
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGame() {
        when(contextProvider.getGameKey(player)).thenReturn(null);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void handleShouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        GameKey gameKey = GameKey.ofTrainingMode();
        ItemStack changeFrom = new ItemStack(Material.IRON_HOE);
        ItemStack changeTo = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItem(1)).thenReturn(changeTo);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(player.getInventory()).thenReturn(inventory);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemChange(player, changeFrom, changeTo)).thenReturn(false);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, ActionHandler.class)).thenReturn(actionHandler);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemChange(player, changeFrom, changeTo);
    }

    @Test
    public void handleShouldNotAlterCancelledEventIfActionHandlerDoesPerformTheAction() {
        GameKey gameKey = GameKey.ofTrainingMode();
        ItemStack changeFrom = new ItemStack(Material.IRON_HOE);
        ItemStack changeTo = new ItemStack(Material.IRON_HOE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItem(1)).thenReturn(changeTo);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(player.getInventory()).thenReturn(inventory);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemChange(player, changeFrom, changeTo)).thenReturn(true);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, ActionHandler.class)).thenReturn(actionHandler);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);
        event.setCancelled(true);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemChange(player, changeFrom, changeTo);
    }
}
