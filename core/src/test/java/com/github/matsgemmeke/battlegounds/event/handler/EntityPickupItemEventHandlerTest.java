package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.event.handler.EntityPickupItemEventHandler;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.junit.Before;
import org.junit.Test;

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

        verify(game, never()).onPickupItem(any(), any());
    }

    @Test
    public void callsGameMethodWhenPlayerHasGamePlayerInstance() {
        Game game = mock(Game.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(gameProvider.getGame(player)).thenReturn(game);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, times(1)).onPickupItem(gamePlayer, event);
    }
}
