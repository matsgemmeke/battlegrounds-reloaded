package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.game.openmode.OpenMode;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameContextProviderTest {

    @Test
    public void addSessionAddsSessionAndReturnsTrue() {
        GameKey gameKey = GameKey.ofSession(1);
        Session session = mock(Session.class);

        GameContextProvider contextProvider = new GameContextProvider();
        boolean added = contextProvider.addSession(gameKey, session);

        assertTrue(added);
    }

    @Test
    public void assignOpenModeAddsOpenModeAndReturnsTrue() {
        OpenMode openMode = mock(OpenMode.class);

        GameContextProvider contextProvider = new GameContextProvider();
        boolean assigned = contextProvider.assignOpenMode(openMode);

        assertTrue(assigned);
    }

    @Test
    public void assignOpenModeDoesNotAddOpenModeAndReturnsFalseWhenAlreadyAssigned() {
        OpenMode openMode = mock(OpenMode.class);
        OpenMode otherOpenMode = mock(OpenMode.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.assignOpenMode(openMode);
        boolean assigned = contextProvider.assignOpenMode(otherOpenMode);

        assertFalse(assigned);
    }

    @Test
    public void getGameContextReturnsOptionalContainingGameContextCorrespondingWithGivenGameKey() {
        GameKey gameKey = GameKey.ofOpenMode();
        GameContext gameContext = new GameContext(gameKey, GameContextType.ARENA_MODE);

        GameContextProvider gameContextProvider = new GameContextProvider();
        gameContextProvider.addGameContext(gameKey, gameContext);
        Optional<GameContext> result = gameContextProvider.getGameContext(gameKey);

        assertThat(result).hasValue(gameContext);
    }

    @Test
    public void getGameContextReturnsEmptyOptionalWhenNoMatchingGameContextsWereFound() {
        GameKey gameKey = GameKey.ofOpenMode();
        GameKey otherKey = GameKey.ofSession(1);
        GameContext gameContext = new GameContext(gameKey, GameContextType.ARENA_MODE);

        GameContextProvider gameContextProvider = new GameContextProvider();
        gameContextProvider.addGameContext(gameKey, gameContext);
        Optional<GameContext> gameContextOptional = gameContextProvider.getGameContext(otherKey);

        assertThat(gameContextOptional).isEmpty();
    }

    @Test
    public void getGameKeysReturnsSetOfAllRegisteredGameKeys() {
        GameKey gameKey = GameKey.ofSession(1);
        Session session = mock(Session.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, session);
        Set<GameKey> gameKeys = contextProvider.getGameKeys();

        assertThat(gameKeys).containsExactly(gameKey);
    }

    @Test
    public void getGameKeyByEntityIdReturnsEmptyOptionalWhenNoLinkOfGivenEntityIdExists() {
        UUID entityId = UUID.randomUUID();

        GameContextProvider contextProvider = new GameContextProvider();
        Optional<GameKey> gameKeyOptional = contextProvider.getGameKeyByEntityId(entityId);

        assertThat(gameKeyOptional).isEmpty();
    }

    @Test
    public void getGameKeyByEntityIdReturnsOptionalWithGameKeyLinkedToGivenEntityId() {
        UUID entityId = UUID.randomUUID();
        GameKey gameKey = GameKey.ofOpenMode();

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerEntity(entityId, gameKey);
        Optional<GameKey> gameKeyOptional = contextProvider.getGameKeyByEntityId(entityId);

        assertThat(gameKeyOptional).hasValue(gameKey);
    }

    @Test
    public void removeSessionReturnsFalseWhenRemovingSessionThatIsNotRegistered() {
        int id = 1;

        GameContextProvider contextProvider = new GameContextProvider();
        boolean removed = contextProvider.removeSession(id);

        assertFalse(removed);
    }

    @Test
    public void removeSessionReturnsTrueWhenRemovingRegisteredSession() {
        int id = 1;
        GameKey gameKey = GameKey.ofSession(id);
        Game game = mock(Game.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);

        boolean removed = contextProvider.removeSession(id);

        assertTrue(removed);
    }

    @Test
    public void sessionExistsReturnsFalseIfNoSessionsWithTheGivenIdAreRegistered() {
        int id = 1;

        GameContextProvider contextProvider = new GameContextProvider();
        boolean sessionExists = contextProvider.sessionExists(id);

        assertFalse(sessionExists);
    }

    @Test
    public void sessionExistsReturnsTrueIfSessionWithGivenIdIsRegistered() {
        int id = 1;
        GameKey gameKey = GameKey.ofSession(id);
        Game game = mock(Game.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);

        boolean sessionExists = contextProvider.sessionExists(id);

        assertTrue(sessionExists);
    }
}
