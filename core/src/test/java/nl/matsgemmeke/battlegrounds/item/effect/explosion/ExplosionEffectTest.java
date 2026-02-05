package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExplosionEffectTest {

    private static final ExplosionProperties PROPERTIES = new ExplosionProperties(null, 2.0f, false, false);
    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID SOURCE_ID = UUID.randomUUID();
    private static final ItemEffectContext CONTEXT = createContext();

    @Mock
    private ExplosionEffectPerformanceFactory explosionEffectPerformanceFactory;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;

    private ExplosionEffect explosionEffect;
    
    @BeforeEach
    void setUp() {
        explosionEffect = new ExplosionEffect(explosionEffectPerformanceFactory, gameContextProvider, GAME_KEY, gameScope);
    }

    @Test
    @DisplayName("startPerformance throws ItemEffectPerformanceException when properties are not set")
    void startPerformance_withoutSetProperties() {
        assertThatThrownBy(() -> explosionEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform explosion effect: properties not set");
    }

    @Test
    @DisplayName("startPerformance throws ItemEffectPerformanceException when there is no game context")
    void startPerformance_withoutGameContext() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        explosionEffect.setProperties(PROPERTIES);

        assertThatThrownBy(() -> explosionEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform explosion effect: no game context for game key OPEN-MODE can be found");
    }

    @Test
    @DisplayName("startPerformance creates and starts performance")
    void startPerformance_successful() {
        ExplosionEffectPerformance performance = mock(ExplosionEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);

        when(explosionEffectPerformanceFactory.create(PROPERTIES)).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        explosionEffect.setProperties(PROPERTIES);
        explosionEffect.startPerformance(CONTEXT);

        verify(performance).setContext(CONTEXT);
        verify(performance).start();
    }

    private static ItemEffectContext createContext() {
        CollisionResult collisionResult = new CollisionResult(null, null, null);
        ItemEffectSource source = mock(ItemEffectSource.class);
        Location startingLocation = new Location(null, 1, 1, 1);

        DamageSource damageSource = mock(DamageSource.class);
        when(damageSource.getUniqueId()).thenReturn(SOURCE_ID);

        return new ItemEffectContext(collisionResult, damageSource, source, startingLocation);
    }
}
