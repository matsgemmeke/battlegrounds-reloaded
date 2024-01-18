package com.github.matsgemmeke.battlegounds;

import com.github.matsgemmeke.battlegrounds.DefaultBattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
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
    public void shouldBeAbleToAddSession() {
        Session session = mock(Session.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.addSession(session));
    }

    @Test
    public void shouldBeAbleToGetContextCollection() {
        FreemodeContext context = mock(FreemodeContext.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.addFreemodeContext(context));
        assertTrue(contextProvider.getContexts().contains(context));
    }

    @Test
    public void shouldBeAbleToGetSessionById() {
        int sessionId = 1;

        Session session = mock(Session.class);
        when(session.getId()).thenReturn(sessionId);

        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.addSession(session));
        assertEquals(session, contextProvider.getSession(sessionId));
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
    public void shouldBeAbleToRemoveSession() {
        Session session = mock(Session.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertFalse(contextProvider.removeSession(session));
        assertTrue(contextProvider.addSession(session));
        assertTrue(contextProvider.removeSession(session));
    }

    @Test
    public void getContextReturnsNullWhenTryingToFindUnknownPlayer() {
        Player player = mock(Player.class);

        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();
        contextProvider.addSession(mock(Session.class));
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
    public void getSessionByPlayer() {
        Player player = mock(Player.class);

        Session session = mock(Session.class);
        when(session.getId()).thenReturn(1);
        when(session.hasPlayer(player)).thenReturn(true);

        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();
        contextProvider.addSession(session);

        assertEquals(session, contextProvider.getContext(player));
    }
}
