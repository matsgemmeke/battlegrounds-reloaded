package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.Key;
import com.google.inject.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ArenaGameContextTest {

    private ArenaGameContext context;

    @BeforeEach
    void setUp() {
        context = new ArenaGameContext(null, null);
    }

    @Test
    @DisplayName("getScopedObject only creates instances once")
    void getScopedObject_createInstanceOnce() {
        Key<String> key = Key.get(String.class);
        AtomicInteger creationCount = new AtomicInteger();

        Provider<String> creator = () -> {
            creationCount.incrementAndGet();
            return "instance";
        };

        String first = context.getScopedObject(key, creator);
        String second = context.getScopedObject(key, creator);

        assertThat(first).isSameAs(second);
        assertThat(creationCount).hasValue(1);
    }

    @Test
    @DisplayName("getScopedObject runs side effects of creator once")
    void getScopedObject_runsSideEffectsOnce() {
        Key<String> key = Key.get(String.class);
        AtomicInteger sideEffectCounter = new AtomicInteger();

        Provider<String> creator = () -> {
            sideEffectCounter.incrementAndGet();
            return "instance";
        };

        context.getScopedObject(key, creator);
        context.getScopedObject(key, creator);

        assertThat(sideEffectCounter).hasValue(1);
    }
}
