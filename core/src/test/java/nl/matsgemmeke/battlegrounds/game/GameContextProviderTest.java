package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.game.arena.Arena;
import nl.matsgemmeke.battlegrounds.game.freeplay.Freeplay;
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
    @DisplayName("addArena adds arena and returns true")
    void addArena() {
        GameKey gameKey = GameKey.ofArena(1);
        Arena arena = mock(Arena.class);

        boolean added = gameContextProvider.addArena(gameKey, arena);

        assertThat(added).isTrue();
    }

    @Test
    @DisplayName("assignFreeplay adds freeplay mode and returns true")
    void assignFreeplay_success() {
        Freeplay freeplay = mock(Freeplay.class);

        boolean assigned = gameContextProvider.assignFreeplay(freeplay);

        assertThat(assigned).isTrue();
    }

    @Test
    @DisplayName("assignFreeplay does not add freeplay mode and returns false when already assigned")
    void assignFreeplay_alreadyAssigned() {
        Freeplay freeplay = mock(Freeplay.class);
        Freeplay otherFreeplay = mock(Freeplay.class);

        gameContextProvider.assignFreeplay(freeplay);
        boolean assigned = gameContextProvider.assignFreeplay(otherFreeplay);

        assertThat(assigned).isFalse();
    }

    @Test
    @DisplayName("getGameContext returns optional with game context corresponding to given game key")
    void getGameContext_matchingGameKey() {
        GameKey gameKey = GameKey.ofFreeplay();
        GameContext gameContext = new GameContext(gameKey, GameContextType.ARENA_MODE);

        gameContextProvider.addGameContext(gameKey, gameContext);
        Optional<GameContext> result = gameContextProvider.getGameContext(gameKey);

        assertThat(result).hasValue(gameContext);
    }

    @Test
    @DisplayName("getGameContext returns empty optional when no matching game contexts were found")
    void getGameContext_noMatches() {
        GameKey gameKey = GameKey.ofFreeplay();
        GameKey otherKey = GameKey.ofArena(1);
        GameContext gameContext = new GameContext(gameKey, GameContextType.ARENA_MODE);

        gameContextProvider.addGameContext(gameKey, gameContext);
        Optional<GameContext> gameContextOptional = gameContextProvider.getGameContext(otherKey);

        assertThat(gameContextOptional).isEmpty();
    }

    @Test
    @DisplayName("getGameKeyByEntityId returns empty optional when no link of given entity id exists")
    void getGameKeyByEntityId_notFound() {
        Optional<GameKey> gameKeyOptional = gameContextProvider.getGameKeyByEntityId(ENTITY_ID);

        assertThat(gameKeyOptional).isEmpty();
    }

    @Test
    @DisplayName("getGameKeyByEntityId returns optional with game key linked to given entity id")
    void getGameKeyByEntityId_success() {
        GameKey gameKey = GameKey.ofFreeplay();

        gameContextProvider.registerEntity(ENTITY_ID, gameKey);
        Optional<GameKey> gameKeyOptional = gameContextProvider.getGameKeyByEntityId(ENTITY_ID);

        assertThat(gameKeyOptional).hasValue(gameKey);
    }

    @Test
    @DisplayName("removeArena returns true when removing a registered arena")
    void removeArena_registeredArena() {
        GameKey gameKey = GameKey.ofArena(ARENA_ID);
        Arena arena = mock(Arena.class);

        gameContextProvider.addArena(gameKey, arena);
        boolean removed = gameContextProvider.removeArena(ARENA_ID);

        assertThat(removed).isTrue();
    }

    @Test
    @DisplayName("removeArena returns false when removing arena that is not registered")
    void removeArena_unregisteredArena() {
        boolean removed = gameContextProvider.removeArena(ARENA_ID);

        assertThat(removed).isFalse();
    }

    @Test
    @DisplayName("arenaExists returns true when an arena by the given id is registered")
    void arenaExists_registered() {
        GameKey gameKey = GameKey.ofArena(ARENA_ID);
        Arena arena = mock(Arena.class);

        gameContextProvider.addArena(gameKey, arena);
        boolean arenaExists = gameContextProvider.arenaExists(ARENA_ID);

        assertThat(arenaExists).isTrue();
    }

    @Test
    @DisplayName("arenaExists returns false when no arenas by the given id is registered")
    void arenaExists_unregistered() {
        boolean arenaExists = gameContextProvider.arenaExists(ARENA_ID);

        assertThat(arenaExists).isFalse();
    }
}
