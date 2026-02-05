package nl.matsgemmeke.battlegrounds.item.effect.damage;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DamageEffectTest {

    private static final DamageProperties PROPERTIES = new DamageProperties(null, null, null);
    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();
    private static final ItemEffectContext CONTEXT = createContext();

    @Mock
    private DamageEffectPerformanceFactory damageEffectPerformanceFactory;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;

    private DamageEffect damageEffect;

    @BeforeEach
    void setUp() {
        damageEffect = new DamageEffect(damageEffectPerformanceFactory, gameContextProvider, GAME_KEY, gameScope);
    }

    @Test
    @DisplayName("getLatestPerformance returns empty optional when no performacnes have been started")
    void getLatestPerformance_withoutStartedPerformances() {
        Optional<ItemEffectPerformance> performanceOptional = damageEffect.getLatestPerformance();

        assertThat(performanceOptional).isEmpty();
    }

    @Test
    @DisplayName("getLatestPerformance returns optional with most recently started ItemEffectPerformance")
    void getLatestPerformanceReturnsOptionalWithLatestStartedItemEffectPerformance() {
        DamageEffectPerformance performance = mock(DamageEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);

        when(damageEffectPerformanceFactory.create(any(DamageProperties.class))).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        damageEffect.setProperties(PROPERTIES);
        damageEffect.startPerformance(CONTEXT);
        Optional<ItemEffectPerformance> performanceOptional = damageEffect.getLatestPerformance();

        assertThat(performanceOptional).hasValue(performance);
    }

    @Test
    @DisplayName("startPerformance throws ItemEffectPerformanceException when properties are not set")
    void startPerformance_withoutSetProperties() {
        assertThatThrownBy(() -> damageEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform damage effect: properties not set");
    }

    @Test
    @DisplayName("startPerformance throws ItemEffectPerformanceException when there is no game context")
    void startPerformance_withoutGameContext() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        damageEffect.setProperties(PROPERTIES);

        assertThatThrownBy(() -> damageEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform damage effect: No game context for game key OPEN-MODE can be found");
    }

    @Test
    @DisplayName("startPerformance creates and starts performance")
    void startPerformance_successful() {
        DamageEffectPerformance performance = mock(DamageEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);

        when(damageEffectPerformanceFactory.create(any(DamageProperties.class))).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        damageEffect.setProperties(PROPERTIES);
        damageEffect.startPerformance(CONTEXT);

        verify(performance).setContext(CONTEXT);
        verify(performance).start();
    }

    @Test
    @DisplayName("rollback cancels ongoing performances")
    void rollback_cancelsPerformances() {
        GameContext gameContext = mock(GameContext.class);

        DamageEffectPerformance performancePerforming = mock(DamageEffectPerformance.class);
        when(performancePerforming.isPerforming()).thenReturn(true);

        DamageEffectPerformance performanceNotPerforming = mock(DamageEffectPerformance.class);
        when(performanceNotPerforming.isPerforming()).thenReturn(false);

        when(damageEffectPerformanceFactory.create(any(DamageProperties.class))).thenReturn(performancePerforming, performanceNotPerforming);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        damageEffect.setProperties(PROPERTIES);
        damageEffect.startPerformance(CONTEXT);
        damageEffect.startPerformance(CONTEXT);
        damageEffect.rollbackPerformances();

        verify(performancePerforming).rollback();
        verify(performanceNotPerforming, never()).rollback();
    }

    private static ItemEffectContext createContext() {
        CollisionResult collisionResult = new CollisionResult(null, null, null);
        ItemEffectSource effectSource = mock(ItemEffectSource.class);
        Location initiationLocation = new Location(null, 1, 1, 1);

        DamageSource damageSource = mock(DamageSource.class);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);

        return new ItemEffectContext(collisionResult, damageSource, effectSource, initiationLocation);
    }
}
