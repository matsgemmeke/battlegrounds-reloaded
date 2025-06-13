package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerSwapHandItemsEventHandlerTest {

    private GameContextProvider contextProvider;
    private Player player;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        player = mock(Player.class);
    }

    @Test
    public void handleShouldDoNothingIfPlayerIsNotInAnyGame() {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, null, null);

        when(contextProvider.getGameKey(player)).thenReturn(null);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(contextProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void handleShouldCancelEventIfActionHandlerDoesNotPerformTheAction() {
        GameKey gameKey = GameKey.ofOpenMode();
        ItemStack swapFrom = new ItemStack(Material.IRON_HOE);
        ItemStack swapTo = new ItemStack(Material.IRON_HOE);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemSwap(player, swapFrom, swapTo)).thenReturn(false);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, ActionHandler.class)).thenReturn(actionHandler);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, swapTo, swapFrom);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemSwap(player, swapFrom, swapTo);
    }

    @Test
    public void handleShouldNotAlterCancelledEventIfActionHandlerDoesPerformTheAction() {
        GameKey gameKey = GameKey.ofOpenMode();
        ItemStack swapFrom = new ItemStack(Material.IRON_HOE);
        ItemStack swapTo = new ItemStack(Material.IRON_HOE);

        ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.handleItemSwap(player, swapFrom, swapTo)).thenReturn(true);

        when(contextProvider.getGameKey(player)).thenReturn(gameKey);
        when(contextProvider.getComponent(gameKey, ActionHandler.class)).thenReturn(actionHandler);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, swapTo, swapFrom);
        event.setCancelled(true);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(contextProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(actionHandler).handleItemSwap(player, swapFrom, swapTo);
    }
}
