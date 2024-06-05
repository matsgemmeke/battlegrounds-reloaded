package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PlayerDropItemEventHandlerTest {

    private GameProvider gameProvider;
    private Item item;
    private Player player;
    private PlayerDropItemEvent event;

    @Before
    public void setUp() {
        this.gameProvider = mock(GameProvider.class);
        this.item = mock(Item.class);
        this.player = mock(Player.class);

        this.event = new PlayerDropItemEvent(player, item);
    }

    @Test
    public void doNothingIfPlayerIsNotInContext() {
        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(gameProvider);
        eventHandler.handle(event);
    }

    @Test
    public void doNothingIfPlayerHasNoGamePlayerInstance() {
        Game game = mock(Game.class);

        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).handleItemDrop(any(), any());
    }

    @Test
    public void shouldCallGameFunctionAndCancelEventAccordingToResult() {
        Game game = mock(Game.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemStack itemStack = mock(ItemStack.class);
        when(item.getItemStack()).thenReturn(itemStack);

        when(game.handleItemDrop(gamePlayer, itemStack)).thenReturn(false);
        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(gameProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(game).handleItemDrop(gamePlayer, itemStack);
    }
}
