package nl.matsgemmeke.battlegrounds.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GameContextProviderTest {

    private static final int ARENA_ID = 1;
    private static final UUID ENTITY_ID = UUID.randomUUID();

    private GameContextProvider gameContextProvider;

    @BeforeEach
    void setUp() {
        gameContextProvider = new GameContextProvider();
    }

    @Test
    @DisplayName("getGameContext returns optional with game context corresponding to given game key")
    void getGameContext_matchingGameKey() {
        GameKey gameKey = GameKey.ofFreeplay();

        GameContext gameContext = mock(GameContext.class);
        when(gameContext.getGameKey()).thenReturn(gameKey);

        gameContextProvider.addGameContext(gameKey, gameContext);
        Optional<GameContext> result = gameContextProvider.getGameContext(gameKey);

        assertThat(result).hasValue(gameContext);
    }

    @Test
    @DisplayName("getGameContext returns empty optional when no matching game contexts were found")
    void getGameContext_noMatches() {
        GameKey gameKey = GameKey.ofFreeplay();
        GameKey otherKey = GameKey.ofArena(1);

        GameContext gameContext = mock(GameContext.class);
        when(gameContext.getGameKey()).thenReturn(gameKey);

        gameContextProvider.addGameContext(gameKey, gameContext);
        Optional<GameContext> gameContextOptional = gameContextProvider.getGameContext(otherKey);

        assertThat(gameContextOptional).isEmpty();
    }

    @Test
    @DisplayName("getGameContext returns empty optional when no link of given entity id exists")
    void getGameContext_notFound() {
        Optional<GameContext> gameContextOptional = gameContextProvider.getGameContext(ENTITY_ID);

        assertThat(gameContextOptional).isEmpty();
    }

    @Test
    @DisplayName("getGameContext returns optional with game context registered to given entity id")
    void getGameContext_registeredEntityId() {
        GameKey gameKey = GameKey.ofFreeplay();
        GameContext gameContext = mock(GameContext.class);

        gameContextProvider.addGameContext(gameKey, gameContext);
        gameContextProvider.registerEntity(ENTITY_ID, gameKey);
        Optional<GameContext> gameContextOptional = gameContextProvider.getGameContext(ENTITY_ID);

        assertThat(gameContextOptional).hasValue(gameContext);
    }

    @Test
    @DisplayName("removeGameContext returns true when the given context is registered")
    void removeGameContext_registeredGameKey() {
        GameKey gameKey = GameKey.ofArena(ARENA_ID);
        GameContext gameContext = mock(GameContext.class);

        gameContextProvider.addGameContext(gameKey, gameContext);
        boolean removed = gameContextProvider.removeGameContext(gameKey);

        assertThat(removed).isTrue();
    }

    @Test
    @DisplayName("removeGameContext returns false when the given context is not registered")
    void removeGameContext_unregisteredGameKey() {
        GameKey gameKey = GameKey.ofFreeplay();

        boolean removed = gameContextProvider.removeGameContext(gameKey);

        assertThat(removed).isFalse();
    }
}
