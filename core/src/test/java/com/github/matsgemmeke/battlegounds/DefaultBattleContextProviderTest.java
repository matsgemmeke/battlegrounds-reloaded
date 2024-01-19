package com.github.matsgemmeke.battlegounds;

import com.github.matsgemmeke.battlegrounds.DefaultBattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.api.game.TrainingMode;
import org.bukkit.entity.Player;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultBattleContextProviderTest {

    @Test
    public void shouldBeAbleToAssignTrainingMode() {
        TrainingMode trainingMode = mock(TrainingMode.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.assignTrainingMode(trainingMode));
    }

    @Test
    public void shouldBeAbleToAddSession() {
        Session session = mock(Session.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.addSession(session));
    }

    @Test
    public void shouldBeAbleToGetContextCollection() {
        TrainingMode trainingMode = mock(TrainingMode.class);
        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();

        assertTrue(contextProvider.assignTrainingMode(trainingMode));
        assertTrue(contextProvider.getContexts().contains(trainingMode));
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
        contextProvider.assignTrainingMode(mock(TrainingMode.class));

        assertNull(contextProvider.getContext(player));
    }

    @Test
    public void getTrainingModeByPlayer() {
        Player player = mock(Player.class);

        TrainingMode trainingMode = mock(TrainingMode.class);
        when(trainingMode.hasPlayer(player)).thenReturn(true);

        DefaultBattleContextProvider contextProvider = new DefaultBattleContextProvider();
        contextProvider.assignTrainingMode(trainingMode);

        assertEquals(trainingMode, contextProvider.getContext(player));
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
