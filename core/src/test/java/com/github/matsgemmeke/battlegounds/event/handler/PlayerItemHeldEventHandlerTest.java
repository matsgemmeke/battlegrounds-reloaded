package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerItemHeldEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

    private Game game;
    private GamePlayer gamePlayer;
    private GameProvider gameProvider;
    private Player player;
    private PlayerItemHeldEvent event;

    @Before
    public void setUp() {
        this.game = mock(Game.class);
        this.gamePlayer = mock(GamePlayer.class);
        this.gameProvider = mock(GameProvider.class);
        this.player = mock(Player.class);

        this.event = new PlayerItemHeldEvent(player, 0, 1);
    }

    @Test
    public void doesNothingWithPlayerNotInAnyContexts() {
        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).onItemHeld(gamePlayer, event);
    }

    @Test
    public void doesNothingWithPlayerWithoutGamePlayerInstance() {
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).onItemHeld(gamePlayer, event);
    }

    @Test
    public void callsGameMethodWhenPlayerHasGamePlayerInstance() {
        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game).onItemHeld(gamePlayer, event);
    }
}
