package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.openmode.OpenMode;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
    public void getComponentThrowsGameKeyNotFoundExceptionIfGivenGameKeyIsNotRegistered() {
        GameKey gameKey = GameKey.ofSession(1);

        GameContextProvider contextProvider = new GameContextProvider();

        assertThrows(GameKeyNotFoundException.class, () -> contextProvider.getComponent(gameKey, TargetFinder.class));
    }

    @Test
    public void getComponentThrowsGameComponentNotFoundExceptionIfComponentIsNotRegistered() {
        GameKey gameKey = GameKey.ofSession(1);
        Game game = mock(Game.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);

        assertThrows(GameComponentNotFoundException.class, () -> contextProvider.getComponent(gameKey, TargetFinder.class));
    }

    @Test
    public void getComponentReturnsComponentInstanceCorrespondingWithGivenComponentClass() {
        GameKey gameKey = GameKey.ofSession(1);
        Game game = mock(Game.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);
        contextProvider.registerComponent(gameKey, TargetFinder.class, targetFinder);

        TargetFinder result = contextProvider.getComponent(gameKey, TargetFinder.class);

        assertEquals(targetFinder, result);
    }

    @Test
    public void getGameKeyReturnsCorrespondingGameKeyForGivenPlayer() {
        Game game = mock(Game.class);
        GameKey gameKey = GameKey.ofOpenMode();
        Player player = mock(Player.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(player)).thenReturn(true);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);
        contextProvider.registerComponent(gameKey, PlayerRegistry.class, playerRegistry);

        GameKey result = contextProvider.getGameKey(player);

        assertEquals(gameKey, result);
    }

    @Test
    public void getGameKeyReturnsNullIfGivenPlayerIsNotInAnyGame() {
        Game game = mock(Game.class);
        GameKey gameKey = GameKey.ofOpenMode();
        Player player = mock(Player.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(player)).thenReturn(false);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);
        contextProvider.registerComponent(gameKey, PlayerRegistry.class, playerRegistry);

        GameKey result = contextProvider.getGameKey(player);

        assertNull(result);
    }

    @Test
    public void getGameKeyReturnsCorrespondingGameKeyForGivenUUID() {
        Game game = mock(Game.class);
        GameKey gameKey = GameKey.ofOpenMode();
        UUID uuid = UUID.randomUUID();

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(uuid)).thenReturn(true);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);
        contextProvider.registerComponent(gameKey, PlayerRegistry.class, playerRegistry);

        GameKey result = contextProvider.getGameKey(uuid);

        assertEquals(gameKey, result);
    }

    @Test
    public void getGameKeyReturnsNullIfGivenUUIDIsNotRegisteredInAnyGame() {
        Game game = mock(Game.class);
        GameKey gameKey = GameKey.ofOpenMode();
        UUID uuid = UUID.randomUUID();

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(uuid)).thenReturn(false);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);
        contextProvider.registerComponent(gameKey, PlayerRegistry.class, playerRegistry);

        GameKey result = contextProvider.getGameKey(uuid);

        assertNull(result);
    }

    @Test
    public void registerComponentThrowsGameKeyNotFoundExceptionWhenRegisteringComponentWithGameKeyThatIsNotRegistered() {
        GameKey gameKey = GameKey.ofSession(1);
        TargetFinder targetFinder = mock(TargetFinder.class);

        GameContextProvider contextProvider = new GameContextProvider();

        assertThrows(GameKeyNotFoundException.class, () -> contextProvider.registerComponent(gameKey, TargetFinder.class, targetFinder));
    }

    @Test
    public void registerComponentAddsGivenComponentToProvider() {
        Game game = mock(Game.class);
        GameKey gameKey = GameKey.ofSession(1);
        TargetFinder targetFinder = mock(TargetFinder.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.registerGame(gameKey, game);
        contextProvider.registerComponent(gameKey, TargetFinder.class, targetFinder);

        TargetFinder result = contextProvider.getComponent(gameKey, TargetFinder.class);

        assertEquals(targetFinder, result);
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

    @Test
    public void shutdownSavesOpenModeState() {
        GameKey gameKey = GameKey.ofOpenMode();
        OpenMode openMode = new OpenMode();
        StatePersistenceHandler statePersistenceHandler = mock(StatePersistenceHandler.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.assignOpenMode(openMode);
        contextProvider.registerComponent(gameKey, StatePersistenceHandler.class, statePersistenceHandler);
        contextProvider.shutdown();

        verify(statePersistenceHandler).saveState();
    }
}
