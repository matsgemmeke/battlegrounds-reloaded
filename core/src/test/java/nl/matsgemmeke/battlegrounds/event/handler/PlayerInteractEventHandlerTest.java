package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
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

    private Game game;
    private GameProvider gameProvider;
    private Player player;

    @Before
    public void setUp() {
        this.game = mock(Game.class);
        this.gameProvider = mock(GameProvider.class);
        this.player = mock(Player.class);
    }

    @Test
    public void shouldDoNothingWithPlayerNotInAnyGames() {
        PlayerInteractEvent event = new PlayerInteractEvent(player, null, null, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).handleItemLeftClick(any(), any());
        verify(game, never()).handleItemRightClick(any(), any());
    }

    @Test
    public void shouldDoNothingWithPlayerWithoutGamePlayerInstance() {
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerInteractEvent event = new PlayerInteractEvent(player, null, null, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).handleItemLeftClick(any(), any());
        verify(game, never()).handleItemRightClick(any(), any());
    }

    @Test
    public void shouldCallLeftClickFunctionAndCancelEventBasedOnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.handleItemLeftClick(gamePlayer, itemStack)).thenReturn(false);

        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, itemStack, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        assertEquals(Result.DENY, event.useItemInHand());

        verify(game).handleItemLeftClick(gamePlayer, itemStack);
    }

    @Test
    public void shouldCallRightClickFunctionAndCancelEventBasedOnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.handleItemRightClick(gamePlayer, itemStack)).thenReturn(true);

        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, itemStack, null, null);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        assertEquals(Result.DEFAULT, event.useItemInHand());

        verify(game).handleItemRightClick(gamePlayer, itemStack);
    }
}
