package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GameScopeTest {

    private static final GameKey GAME_KEY = GameKey.ofFreeplay();
    private static final GameContextType TYPE = GameContextType.FREEPLAY_MODE;
    private static final Key<String> KEY = Key.get(String.class);

    private GameScope scope;

    @BeforeEach
    void setUp() {
        scope = new GameScope();
    }

    @Test
    @DisplayName("scope throws OutOfScopeException when no game context is entered")
    void scope_noContextEntered() {
        Provider<String> unscoped = () -> "value";
        Provider<String> scoped = scope.scope(KEY, unscoped);

        assertThatThrownBy(scoped::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessage("Cannot access java.lang.String because no GameContext is active in GameScope");
    }

    @Test
    @DisplayName("scope restores previous game context when performing a runnable while another is already actives")
    void scope_nested() {
        GameContext freeplay = this.createGameContext("freeplay");
        GameContext arena = this.createGameContext("arena");

        scope.runInScope(freeplay, () -> {
            assertThat(scope.getCurrentGameContext()).contains(freeplay);

            scope.runInScope(arena, () -> assertThat(scope.getCurrentGameContext()).contains(arena));

            assertThat(scope.getCurrentGameContext()).contains(freeplay);
        });

        assertThat(scope.getCurrentGameContext()).isEmpty();
    }

    @Test
    @DisplayName("runInScope performs runnable and cleans up top level scope")
    void runInScope_successful() {
        GameContext freeplay = this.createGameContext("freeplay");

        scope.runInScope(freeplay, () -> {});

        assertThat(scope.getCurrentGameContext()).isEmpty();
    }

    @Test
    @DisplayName("runInScope restores previous context when an exception occurs in nested runnable")
    void runInScope_restoresPreviousContext() {
        GameContext freeplay = this.createGameContext("freeplay");
        GameContext arena = this.createGameContext("arena");

        scope.runInScope(freeplay, () -> {
            assertThatThrownBy(() ->
                    scope.runInScope(arena, () -> {
                        throw new RuntimeException("boom");
                    })
            ).isInstanceOf(RuntimeException.class);

            assertThat(scope.getCurrentGameContext()).contains(freeplay);
        });
    }

    private GameContext createGameContext(String label) {
        Map<Key<?>, Object> cache = new HashMap<>();

        return new GameContext(GAME_KEY, TYPE) {
            @SuppressWarnings("unchecked")
            public <T> T getScopedObject(Key<T> k, Provider<T> unscoped) {
                // Ignore the real `unscoped` provider entirely, and just
                // return `label` itself as the "scoped object" for any key.
                return (T) cache.computeIfAbsent(k, kk -> label);
            }
        };
    }
}
