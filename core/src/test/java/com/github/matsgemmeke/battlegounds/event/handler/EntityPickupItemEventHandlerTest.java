package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.event.handler.EntityPickupItemEventHandler;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EntityPickupItemEventHandlerTest {

    private BattleContextProvider contextProvider;
    private Item item;

    @Before
    public void setUp() {
        this.contextProvider = mock(BattleContextProvider.class);
        this.item = mock(Item.class);
    }

    @Test
    public void doNothingIfEntityIsNotPlayer() {
        Zombie zombie = mock(Zombie.class);

        EntityPickupItemEvent event = new EntityPickupItemEvent(zombie, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);
    }

    @Test
    public void doNothingIfPlayerIsNotInContext() {
        Player player = mock(Player.class);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);
    }

    @Test
    public void doNothingIfPlayerHasNoBattlePlayerInstance() {
        BattleContext context = mock(BattleContext.class);
        Player player = mock(Player.class);

        when(contextProvider.getContext(player)).thenReturn(context);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(context, never()).onPickupItem(any(), any());
    }

    @Test
    public void callsContextMethodWhenPlayerHasBattlePlayerInstance() {
        BattleContext context = mock(BattleContext.class);
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Player player = mock(Player.class);

        when(context.getBattlePlayer(player)).thenReturn(battlePlayer);
        when(contextProvider.getContext(player)).thenReturn(context);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        EntityPickupItemEventHandler eventHandler = new EntityPickupItemEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(context, times(1)).onPickupItem(battlePlayer, event);
    }
}
