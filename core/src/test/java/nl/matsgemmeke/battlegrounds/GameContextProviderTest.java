package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.registry.PlayerRegistry;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameContextProviderTest {

    @Test
    public void shouldAddSessionContextAndReturnAddedInstance() {
        GameContext sessionContext = mock(GameContext.class);

        GameContextProvider contextProvider = new GameContextProvider();
        boolean added = contextProvider.addSessionContext(1, sessionContext);

        assertTrue(added);
    }

    @Test
    public void shouldAssignTrainingModeContext() {
        GameContext trainingModeContext = mock(GameContext.class);

        GameContextProvider contextProvider = new GameContextProvider();
        boolean assigned = contextProvider.assignTrainingModeContext(trainingModeContext);

        assertTrue(assigned);
    }

    @Test
    public void shouldOnlyBeAbleToSetTrainingModeContextOnce() {
        GameContext trainingModeContext = mock(GameContext.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.assignTrainingModeContext(trainingModeContext);

        boolean assigned = contextProvider.assignTrainingModeContext(mock(GameContext.class));

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
    @SuppressWarnings("unchecked")
    public void shouldReturnContextWhosePlayerRegistryHasPlayer() {
        Player player = mock(Player.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(player)).thenReturn(true);

        GameContext context = mock(GameContext.class);
        when(context.getPlayerRegistry()).thenReturn(playerRegistry);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.assignTrainingModeContext(context);

        GameContext result = contextProvider.getContext(player);

        assertEquals(context, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnNullContextWhenPlayerIsNotInAnyGame() {
        Player player = mock(Player.class);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(player)).thenReturn(false);

        GameContext context = mock(GameContext.class);
        when(context.getPlayerRegistry()).thenReturn(playerRegistry);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.addSessionContext(1, context);

        GameContext result = contextProvider.getContext(player);

        assertNull(result);
    }

    @Test
    public void shouldReturnContextWhoseEntityRegistriesContainsEntityWithMatchingUUID() {
        UUID uuid = UUID.randomUUID();

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(uuid)).thenReturn(true);

        GameContext context = mock(GameContext.class);
        when(context.getPlayerRegistry()).thenReturn(playerRegistry);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.addSessionContext(1, context);

        GameContext result = contextProvider.getContext(uuid);

        assertEquals(context, result);
    }

    @Test
    public void shouldReturnNullContextWhenEntityIsNotInAnyGame() {
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        UUID uuid = UUID.randomUUID();

        Item item = mock(Item.class);
        when(item.getUniqueId()).thenReturn(uuid);

        GameContext context = mock(GameContext.class);
        when(context.getPlayerRegistry()).thenReturn(playerRegistry);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.addSessionContext(1, context);

        GameContext result = contextProvider.getContext(uuid);

        assertNull(result);
    }

    @Test
    public void shouldGetSessionById() {
        int sessionId = 1;

        GameContext sessionContext = mock(GameContext.class);

        GameContextProvider contextProvider = new GameContextProvider();
        contextProvider.addSessionContext(sessionId, sessionContext);

        GameContext result = contextProvider.getSessionContext(sessionId);

        assertEquals(sessionContext, result);
    }

    @Test
    public void registerComponentThrowsGameKeyNotFoundExceptionWhenRegisteringComponentWithGameKeyThatIsNotRegistered() {
        GameKey gameKey = GameKey.ofSession(1);
        TargetFinder targetFinder = mock(TargetFinder.class);

        GameContextProvider contextProvider = new GameContextProvider();

        assertThrows(GameKeyNotFoundException.class, () -> contextProvider.registerComponent(gameKey, TargetFinder.class, targetFinder));
    }

    @Test
    public void shouldRemoveSession() {
        int sessionId = 1;

        GameContext sessionContext = mock(GameContext.class);
        GameContextProvider contextProvider = new GameContextProvider();

        assertFalse(contextProvider.removeSessionContext(sessionId));
        assertTrue(contextProvider.addSessionContext(sessionId, sessionContext));
        assertTrue(contextProvider.removeSessionContext(sessionId));
    }
}
