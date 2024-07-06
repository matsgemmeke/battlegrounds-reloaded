package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultActionHandlerTest {

    private Game game;
    private ItemStack itemStack;
    private Player player;

    @Before
    public void setUp() {
        game = mock(Game.class);
        itemStack = new ItemStack(Material.IRON_HOE);
        player = mock(Player.class);
    }

    @Test
    public void shouldAllowLeftClickIfGameDoesNotHavePlayer() {
        when(game.getGamePlayer(player)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean result = actionHandler.handleItemLeftClick(player, itemStack);

        assertTrue(result);
    }

    @Test
    public void shouldPassOnLeftClickToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(false);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(true);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean result = actionHandler.handleItemLeftClick(player, itemStack);

        assertFalse(result);

        verify(behavior1).handleLeftClickAction(gamePlayer, itemStack);
        verify(behavior2).handleLeftClickAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldAllowRightClickIfGameDoesNotHavePlayer() {
        when(game.getGamePlayer(player)).thenReturn(null);

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean result = actionHandler.handleItemRightClick(player, itemStack);

        assertTrue(result);
    }

    @Test
    public void shouldPassOnRightClickToItemBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(false);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(true);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.getItemBehaviors()).thenReturn(List.of(behavior1, behavior2));

        DefaultActionHandler actionHandler = new DefaultActionHandler(game);

        boolean result = actionHandler.handleItemRightClick(player, itemStack);

        assertFalse(result);

        verify(behavior1).handleRightClickAction(gamePlayer, itemStack);
        verify(behavior2).handleRightClickAction(gamePlayer, itemStack);
    }
}
