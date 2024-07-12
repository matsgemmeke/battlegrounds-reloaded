package nl.matsgemmeke.battlegrounds.game.access.provider;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.access.DefaultActionHandler;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.access.provider.ActionHandlerProvider;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionHandlerProviderTest {

    private GameProvider gameProvider;
    private Player player;

    @Before
    public void setUp() {
        gameProvider = mock(GameProvider.class);
        player = mock(Player.class);
    }

    @Test
    public void shouldReturnNullIfPlayerIsNotInAnyGame() {
        when(gameProvider.getGame(player)).thenReturn(null);

        ActionHandlerProvider actionHandlerProvider = new ActionHandlerProvider(gameProvider);
        ActionHandler result = actionHandlerProvider.getActionHandler(player);

        assertNull(result);
    }

    @Test
    public void shouldReturnNewActionHandlerInstanceWhenPlayerIsInGame() {
        Game game = mock(Game.class);

        when(gameProvider.getGame(player)).thenReturn(game);

        ActionHandlerProvider actionHandlerProvider = new ActionHandlerProvider(gameProvider);
        ActionHandler result = actionHandlerProvider.getActionHandler(player);

        assertTrue(result instanceof DefaultActionHandler);
    }

    @Test
    public void shouldReturnSameInstanceWhenGettingActionHandlerForSameGame() {
        Game game = mock(Game.class);

        when(gameProvider.getGame(player)).thenReturn(game);

        ActionHandlerProvider actionHandlerProvider = new ActionHandlerProvider(gameProvider);
        ActionHandler result1 = actionHandlerProvider.getActionHandler(player);
        ActionHandler result2 = actionHandlerProvider.getActionHandler(player);

        assertEquals(result1, result2);
    }
}
