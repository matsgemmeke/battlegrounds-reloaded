package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.entity.DefaultBattlePlayer;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerItemHeldEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

    private BattlePlayer battlePlayer;
    private Game game;
    private GameProvider gameProvider;
    private Player player;
    private PlayerItemHeldEvent event;

    @Before
    public void setUp() {
        this.game = mock(Game.class);
        this.gameProvider = mock(GameProvider.class);
        this.player = mock(Player.class);

        this.battlePlayer = new DefaultBattlePlayer(player);
        this.event = new PlayerItemHeldEvent(player, 0, 1);
    }

    @Test
    public void doesNothingWithPlayerNotInAnyContexts() {
        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).onItemHeld(battlePlayer, event);
    }

    @Test
    public void doesNothingWithPlayerWithoutBattlePlayerInstance() {
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game, never()).onItemHeld(battlePlayer, event);
    }

    @Test
    public void callsBattleContextMethodWhenPlayerHasBattlePlayerInstance() {
        when(game.getBattlePlayer(player)).thenReturn(battlePlayer);
        when(gameProvider.getGame(player)).thenReturn(game);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(gameProvider);
        eventHandler.handle(event);

        verify(game).onItemHeld(battlePlayer, event);
    }
}
