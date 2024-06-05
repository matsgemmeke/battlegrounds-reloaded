package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EntityPickupItemEventHandlerTest {

    private GameProvider gameProvider;
    private Item item;

    @Before
    public void setUp() {
        this.gameProvider = mock(GameProvider.class);
        this.item = mock(Item.class);
    }

    @Test
    public void doNothingIfEntityIsNotPlayer() {
        Zombie zombie = mock(Zombie.class);

        EntityPickupItemEvent event = new EntityPickupItemEvent(zombie, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(gameProvider);
        eventHandler.handle(event);
    }

    @Test
    public void doNothingIfPlayerIsNotInAnyGame() {
        Player player = mock(Player.class);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(gameProvider);
        eventHandler.handle(event);
    }

    @Test
    public void doNothingIfPlayerHasNoGamePlayerInstance() {
        Game game = mock(Game.class);
        Player player = mock(Player.class);

        when(gameProvider.getGame(player)).thenReturn(game);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).handleItemPickup(any(), any());
    }

    @Test
    public void shouldCallGameActionMethodAndCancelEventBasedOnResult() {
        Game game = mock(Game.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        ItemStack itemStack = mock(ItemStack.class);
        when(item.getItemStack()).thenReturn(itemStack);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.handleItemPickup(gamePlayer, itemStack)).thenReturn(false);

        when(gameProvider.getGame(player)).thenReturn(game);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(gameProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());

        verify(game).handleItemPickup(gamePlayer, itemStack);
    }
}
