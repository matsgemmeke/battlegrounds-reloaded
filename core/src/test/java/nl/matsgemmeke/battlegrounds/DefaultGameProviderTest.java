package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultGameProviderTest {

    @Test
    public void shouldBeAbleToAssignTrainingMode() {
        TrainingMode trainingMode = mock(TrainingMode.class);
        DefaultGameProvider gameProvider = new DefaultGameProvider();

        assertTrue(gameProvider.assignTrainingMode(trainingMode));
    }

    @Test
    public void shouldOnlyBeAbleToSetTrainingModeOnce() {
        TrainingMode trainingMode = mock(TrainingMode.class);

        DefaultGameProvider gameProvider = new DefaultGameProvider();
        gameProvider.assignTrainingMode(trainingMode);

        boolean assignNewTrainingMode = gameProvider.assignTrainingMode(mock(TrainingMode.class));

        assertFalse(assignNewTrainingMode);
    }

    @Test
    public void shouldBeAbleToAddSession() {
        Session session = mock(Session.class);
        DefaultGameProvider gameProvider = new DefaultGameProvider();

        assertTrue(gameProvider.addSession(session));
    }

    @Test
    public void shouldBeAbleToGetContextCollection() {
        TrainingMode trainingMode = mock(TrainingMode.class);
        DefaultGameProvider gameProvider = new DefaultGameProvider();

        assertTrue(gameProvider.assignTrainingMode(trainingMode));
        assertTrue(gameProvider.getGames().contains(trainingMode));
    }

    @Test
    public void shouldBeAbleToGetSessionById() {
        int sessionId = 1;

        Session session = mock(Session.class);
        when(session.getId()).thenReturn(sessionId);

        DefaultGameProvider gameProvider = new DefaultGameProvider();

        assertTrue(gameProvider.addSession(session));
        assertEquals(session, gameProvider.getSession(sessionId));
    }

    @Test
    public void shouldBeAbleToRemoveSession() {
        Session session = mock(Session.class);
        DefaultGameProvider gameProvider = new DefaultGameProvider();

        assertFalse(gameProvider.removeSession(session));
        assertTrue(gameProvider.addSession(session));
        assertTrue(gameProvider.removeSession(session));
    }

    @Test
    public void getContextReturnsNullWhenTryingToFindUnknownPlayer() {
        Player player = mock(Player.class);

        DefaultGameProvider gameProvider = new DefaultGameProvider();
        gameProvider.addSession(mock(Session.class));
        gameProvider.assignTrainingMode(mock(TrainingMode.class));

        assertNull(gameProvider.getGame(player));
    }

    @Test
    public void getTrainingModeByPlayer() {
        Player player = mock(Player.class);

        TrainingMode trainingMode = mock(TrainingMode.class);
        when(trainingMode.hasPlayer(player)).thenReturn(true);

        DefaultGameProvider gameProvider = new DefaultGameProvider();
        gameProvider.assignTrainingMode(trainingMode);

        assertEquals(trainingMode, gameProvider.getGame(player));
    }

    @Test
    public void getSessionByPlayer() {
        Player player = mock(Player.class);

        Session session = mock(Session.class);
        when(session.getId()).thenReturn(1);
        when(session.hasPlayer(player)).thenReturn(true);

        DefaultGameProvider gameProvider = new DefaultGameProvider();
        gameProvider.addSession(session);

        assertEquals(session, gameProvider.getGame(player));
    }

    @Test
    public void shouldReturnTrainingModeIfEntityIsInTrainingMode() {
        Entity entity = mock(Entity.class);

        TrainingMode trainingMode = mock(TrainingMode.class);
        when(trainingMode.hasEntity(entity)).thenReturn(true);

        DefaultGameProvider gameProvider = new DefaultGameProvider();
        gameProvider.assignTrainingMode(trainingMode);

        Game game = gameProvider.getGame(entity);

        assertEquals(trainingMode, game);
    }

    @Test
    public void shouldReturnSessionIfEntityIsInSession() {
        Entity entity = mock(Entity.class);

        Session session = mock(Session.class);
        when(session.hasEntity(entity)).thenReturn(true);

        DefaultGameProvider gameProvider = new DefaultGameProvider();
        gameProvider.addSession(session);

        Game game = gameProvider.getGame(entity);

        assertEquals(session, game);
    }

    @Test
    public void shouldReturnNullIfNoneOfTheGamesHasEntity() {
        Entity entity = mock(Entity.class);

        DefaultGameProvider gameProvider = new DefaultGameProvider();
        gameProvider.assignTrainingMode(mock(TrainingMode.class));
        gameProvider.addSession(mock(Session.class));

        Game game = gameProvider.getGame(entity);

        assertNull(game);
    }
}
