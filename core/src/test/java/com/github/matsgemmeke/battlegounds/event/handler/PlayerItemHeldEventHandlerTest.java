package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.entity.DefaultBattlePlayer;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerItemHeldEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerItemHeldEventHandlerTest {

    private BattleContext context;
    private BattleContextProvider contextProvider;
    private BattlePlayer battlePlayer;
    private Player player;
    private PlayerItemHeldEvent event;

    @Before
    public void setUp() {
        this.context = mock(BattleContext.class);
        this.contextProvider = mock(BattleContextProvider.class);
        this.player = mock(Player.class);

        this.battlePlayer = new DefaultBattlePlayer(player);
        this.event = new PlayerItemHeldEvent(player, 0, 1);
    }

    @Test
    public void doesNothingWithPlayerNotInAnyContexts() {
        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(context, never()).onItemHeld(battlePlayer, event);
    }

    @Test
    public void doesNothingWithPlayerWithoutBattlePlayerInstance() {
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(context, never()).onItemHeld(battlePlayer, event);
    }

    @Test
    public void callsBattleContextMethodWhenPlayerHasBattlePlayerInstance() {
        when(context.getBattlePlayer(player)).thenReturn(battlePlayer);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerItemHeldEventHandler eventHandler = new PlayerItemHeldEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(context).onItemHeld(battlePlayer, event);
    }
}
