package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.entity.DefaultBattlePlayer;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerInteractEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerInteractEventHandlerTest {

    private Game game;
    private GameProvider gameProvider;
    private BattlePlayer battlePlayer;
    private Player player;
    private PlayerInteractEvent event;

    @Before
    public void setUp() {
        this.game = mock(Game.class);
        this.gameProvider = mock(GameProvider.class);
        this.player = mock(Player.class);

        this.battlePlayer = new DefaultBattlePlayer(player);
        this.event = new PlayerInteractEvent(player, null, null, null, null);
    }

    @Test
    public void doesNothingWithPlayerNotInAnyGames() {
        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).onInteract(battlePlayer, event);
    }

    @Test
    public void doesNothingWithPlayerWithoutBattlePlayerInstance() {
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).onInteract(battlePlayer, event);
    }

    @Test
    public void callsGameMethodWhenPlayerHasBattlePlayerInstance() {
        when(game.getBattlePlayer(player)).thenReturn(battlePlayer);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerInteractEventHandler eventHandler = new PlayerInteractEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game).onInteract(battlePlayer, event);
    }
}
