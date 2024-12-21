package nl.matsgemmeke.battlegrounds;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
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
    @SuppressWarnings("unchecked")
    public void shouldReturnContextWhosePlayerRegistryHasPlayer() {
        Player player = mock(Player.class);

        EntityRegistry<GamePlayer, Player> playerRegistry = (EntityRegistry<GamePlayer, Player>) mock(EntityRegistry.class);
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

        EntityRegistry<GamePlayer, Player> playerRegistry = (EntityRegistry<GamePlayer, Player>) mock(EntityRegistry.class);
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

        EntityRegistry<GamePlayer, Player> playerRegistry = mock();
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
        UUID uuid = UUID.randomUUID();

        Item item = mock(Item.class);
        when(item.getUniqueId()).thenReturn(uuid);

        EntityRegistry<GamePlayer, Player> playerRegistry = mock();

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
    public void shouldRemoveSession() {
        int sessionId = 1;

        GameContext sessionContext = mock(GameContext.class);
        GameContextProvider contextProvider = new GameContextProvider();

        assertFalse(contextProvider.removeSessionContext(sessionId));
        assertTrue(contextProvider.addSessionContext(sessionId, sessionContext));
        assertTrue(contextProvider.removeSessionContext(sessionId));
    }
}
