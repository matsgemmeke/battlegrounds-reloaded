package com.github.matsgemmeke.battlegounds;

import com.github.matsgemmeke.battlegrounds.DefaultBattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import org.bukkit.entity.Player;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultBattleContextProviderTest {

    @Test
    public void shouldBeAbleToAddFreemodeContext() {
        FreemodeContext context = mock(FreemodeContext.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.addFreemodeContext(context));
    }

    @Test
    public void shouldBeAbleToAddGame() {
        GameContext context = mock(GameContext.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.addGameContext(context));
    }

    @Test
    public void shouldBeAbleToGetContextCollection() {
        FreemodeContext context = mock(FreemodeContext.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.addFreemodeContext(context));
        assertTrue(contextProvider.getContexts().contains(context));
    }

    @Test
    public void shouldBeAbleToGetGameById() {
        int gameId = 1;

        GameContext context = mock(GameContext.class);
        when(context.getId()).thenReturn(gameId);

        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.addGameContext(context));
        assertEquals(context, contextProvider.getGameContext(gameId));
    }

    @Test
    public void shouldBeAbleToRemoveContext() {
        FreemodeContext context = mock(FreemodeContext.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertFalse(contextProvider.removeFreemodeContext(context));
        assertTrue(contextProvider.addFreemodeContext(context));
        assertTrue(contextProvider.removeFreemodeContext(context));
    }

    @Test
    public void shouldBeAbleToRemoveGame() {
        GameContext context = mock(GameContext.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertFalse(contextProvider.removeGameContext(context));
        assertTrue(contextProvider.addGameContext(context));
        assertTrue(contextProvider.removeGameContext(context));
    }

    @Test
    public void getContextReturnsNullWhenTryingToFindUnknownPlayer() {
        Player player = mock(Player.class);

        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();
        contextProvider.addGameContext(mock(GameContext.class));
        contextProvider.addFreemodeContext(mock(FreemodeContext.class));

        assertNull(contextProvider.getContext(player));
    }

    @Test
    public void getFreemodeContextByPlayer() {
        Player player = mock(Player.class);

        FreemodeContext context = mock(FreemodeContext.class);
        when(context.hasPlayer(player)).thenReturn(true);

        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();
        contextProvider.addFreemodeContext(context);

        assertEquals(context, contextProvider.getContext(player));
    }

    @Test
    public void getGameContextByPlayer() {
        Player player = mock(Player.class);

        GameContext context = mock(GameContext.class);
        when(context.getId()).thenReturn(1);
        when(context.hasPlayer(player)).thenReturn(true);

        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();
        contextProvider.addGameContext(context);

        assertEquals(context, contextProvider.getContext(player));
    }
}
