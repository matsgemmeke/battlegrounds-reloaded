package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerInteractEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerInteractEventHandlerTest {

    private Game game;
    private GamePlayer gamePlayer;
    private GameProvider gameProvider;
    private Player player;
    private PlayerInteractEvent event;

    @Before
    public void setUp() {
        this.game = mock(Game.class);
        this.gameProvider = mock(GameProvider.class);
        this.player = mock(Player.class);

        this.gamePlayer = new DefaultGamePlayer(player);
        this.event = new PlayerInteractEvent(player, null, null, null, null);
    }

    @Test
    public void doesNothingWithPlayerNotInAnyGames() {
        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).onInteract(gamePlayer, event);
    }

    @Test
    public void doesNothingWithPlayerWithoutGamePlayerInstance() {
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).onInteract(gamePlayer, event);
    }

    @Test
    public void callsGameMethodWhenPlayerHasGamePlayerInstance() {
        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game).onInteract(gamePlayer, event);
    }
}
