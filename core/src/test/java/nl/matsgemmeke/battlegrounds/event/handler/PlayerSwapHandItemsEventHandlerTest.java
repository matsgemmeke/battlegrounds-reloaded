package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerSwapHandItemsEventHandlerTest {

    private GameProvider gameProvider;
    private Player player;

    @Before
    public void setUp() {
        this.gameProvider = mock(GameProvider.class);
        this.player = mock(Player.class);
    }

    @Test
    public void shouldDoNothingIfPlayerIsNotInAnyGame() {
        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, null, null);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(gameProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldDoNothingIfGameHasNoGamePlayerInstance() {
        Game game = mock(Game.class);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, null, null);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(gameProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCallGameFunctionIfGameHasGamePlayerInstance() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemStack swapFrom = mock(ItemStack.class);
        ItemStack swapTo = mock(ItemStack.class);

        Game game = mock(Game.class);
        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.handleItemSwap(gamePlayer, swapFrom, swapTo)).thenReturn(false);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerSwapHandItemsEvent event = new PlayerSwapHandItemsEvent(player, swapTo, swapFrom);

        PlayerSwapHandItemsEventHandler eventHandler = new PlayerSwapHandItemsEventHandler(gameProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(game).handleItemSwap(gamePlayer, swapFrom, swapTo);
    }
}
