package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SmokeScreenEffectTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();
    private static final ItemEffectContext CONTEXT = createContext();
    private static final SmokeScreenProperties PROPERTIES = new SmokeScreenProperties(null, null, 100L, 200L, 2.0, 1.0, 5.0, 0.1, 5L);

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private SmokeScreenEffectPerformanceFactory smokeScreenEffectPerformanceFactory;

    private SmokeScreenEffect smokeScreenEffect;

    @BeforeEach
    void setUp() {
        smokeScreenEffect = new SmokeScreenEffect(gameContextProvider, GAME_KEY, gameScope, smokeScreenEffectPerformanceFactory);
    }

    @Test
    @DisplayName("startPerformance throws ItemEffectPerformanceException when properties are not set")
    void startPerformance_withoutSetProperties() {
        assertThatThrownBy(() -> smokeScreenEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform smoke screen effect: properties not set");
    }

    @Test
    @DisplayName("startPerformance throws ItemEffectPerformanceException when there is no game context")
    void startPerformance_withoutGameContext() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        smokeScreenEffect.setProperties(PROPERTIES);

        assertThatThrownBy(() -> smokeScreenEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform smoke screen effect: no game context for game key OPEN-MODE can be found");
    }

    @Test
    @DisplayName("startPerformance creates and starts performance")
    void startPerformance_successful() {
        SmokeScreenEffectPerformance performance = mock(SmokeScreenEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });
        when(smokeScreenEffectPerformanceFactory.create(PROPERTIES)).thenReturn(performance);

        smokeScreenEffect.setProperties(PROPERTIES);
        smokeScreenEffect.startPerformance(CONTEXT);

        verify(performance).setContext(CONTEXT);
        verify(performance).start();
    }

    private static ItemEffectContext createContext() {
        CollisionResult collisionResult = new CollisionResult(null, null, null);
        Actor actor = mock(Actor.class);
        Location startingLocation = new Location(null, 1, 1, 1);

        DamageSource damageSource = mock(DamageSource.class);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);

        return new ItemEffectContext(collisionResult, damageSource, actor, startingLocation);
    }
}
