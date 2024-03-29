package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerDropItemEventHandler;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.junit.Before;
import org.junit.Test;

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

        verify(game, never()).onItemDrop(any(), any());
    }

    @Test
    public void sendEventToContext() {
        Game game = mock(Game.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(game.onItemDrop(gamePlayer, event)).thenReturn(false);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, times(1)).onItemDrop(gamePlayer, event);
    }
}
