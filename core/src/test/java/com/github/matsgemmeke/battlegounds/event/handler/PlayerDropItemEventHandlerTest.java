package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerDropItemEventHandler;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerDropItemEventHandlerTest {

    private BattleContextProvider contextProvider;
    private Item item;
    private Player player;
    private PlayerDropItemEvent event;

    @Before
    public void setUp() {
        this.contextProvider = mock(BattleContextProvider.class);
        this.item = mock(Item.class);
        this.player = mock(Player.class);

        this.event = new PlayerDropItemEvent(player, item);
    }

    @Test
    public void doNothingIfPlayerIsNotInContext() {
        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);
    }

    @Test
    public void doNothingIfPlayerHasNoBattlePlayerInstance() {
        BattleContext context = mock(BattleContext.class);

        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(context, never()).onItemDrop(any(), any());
    }

    @Test
    public void sendEventToContext() {
        BattleContext context = mock(BattleContext.class);
        BattlePlayer battlePlayer = mock(BattlePlayer.class);

        when(context.getBattlePlayer(player)).thenReturn(battlePlayer);
        when(context.onItemDrop(battlePlayer, event)).thenReturn(false);
        when(contextProvider.getContext(player)).thenReturn(context);

        PlayerDropItemEventHandler eventHandler = new PlayerDropItemEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(context, times(1)).onItemDrop(battlePlayer, event);
    }
}
