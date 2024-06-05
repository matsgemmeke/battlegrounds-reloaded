package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

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
    public void doesNothingWithPlayerNotInAnyContexts() {
        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).handleItemChange(any(), any(), any());
    }

    @Test
    public void doesNothingWithPlayerWithoutGamePlayerInstance() {
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).handleItemChange(any(), any(), any());
    }

    @Test
    public void shouldCallGameFunctionWhenPlayerHasGamePlayerInstance() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack changeFrom = mock(ItemStack.class);
        ItemStack changeTo = mock(ItemStack.class);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItem(1)).thenReturn(changeTo);
        when(inventory.getItemInMainHand()).thenReturn(changeFrom);
        when(player.getInventory()).thenReturn(inventory);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game).handleItemChange(gamePlayer, changeFrom, changeTo);
    }
}
