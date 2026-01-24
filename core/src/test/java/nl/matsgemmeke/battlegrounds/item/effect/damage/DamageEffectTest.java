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
import nl.matsgemmeke.battlegrounds.item.trigger.*;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    void getLatestPerformanceReturnsEmptyOptionalWhenNoPerformancesHaveStarted() {
        Optional<ItemEffectPerformance> performanceOptional = damageEffect.getLatestPerformance();

        assertThat(performanceOptional).isEmpty();
    }

    @Test
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
    void startPerformanceThrowsItemEffectPerformanceExceptionWhenPropertiesAreNotSet() {
        assertThatThrownBy(() -> damageEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform damage effect: properties not set");
    }

    @Test
    void startPerformanceThrowsItemEffectPerformanceExceptionWhenThereIsNoGameContext() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        damageEffect.setProperties(PROPERTIES);

        assertThatThrownBy(() -> damageEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform damage effect: No game context for game key OPEN-MODE can be found");
    }

    @Test
    void startPerformanceCreatesAndStartsTriggerRunsWithObserversThatStartsPerformance() {
        DamageEffectPerformance performance = mock(DamageEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);
        TriggerRun triggerRun = mock(TriggerRun.class);
        TriggerResult triggerResult = mock(TriggerResult.class);

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        when(damageEffectPerformanceFactory.create(any(DamageProperties.class))).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        damageEffect.addTriggerExecutor(triggerExecutor);
        damageEffect.setProperties(PROPERTIES);
        damageEffect.startPerformance(CONTEXT);

        ArgumentCaptor<TriggerContext> triggerContextCaptor = ArgumentCaptor.forClass(TriggerContext.class);
        verify(triggerExecutor).createTriggerRun(triggerContextCaptor.capture());

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());
        triggerObserverCaptor.getValue().onActivate(triggerResult);

        assertThat(triggerContextCaptor.getValue()).satisfies(triggerContext -> {
            assertThat(triggerContext.sourceId()).isEqualTo(DAMAGE_SOURCE_ID);
            assertThat(triggerContext.actor()).isEqualTo(CONTEXT.getTriggerTarget());
        });

        verify(triggerRun).start();
        verify(performance).addTriggerRun(triggerRun);
        verify(performance).setContext(CONTEXT);
        verify(performance).start();
    }

    @Test
    void startPerformanceCreatesAndStartsPerformanceWhenNoTriggerExecutorsAreAdded() {
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

        ArgumentCaptor<DamageProperties> damagePropertiesCaptor = ArgumentCaptor.forClass(DamageProperties.class);
        verify(damageEffectPerformanceFactory).create(damagePropertiesCaptor.capture());

        verify(performance, never()).addTriggerRun(any(TriggerRun.class));
        verify(performance).setContext(CONTEXT);
        verify(performance).start();
    }

    @Test
    void rollbackCancelsOngoingPerformances() {
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
        TriggerTarget triggerTarget = mock(TriggerTarget.class);
        Location initiationLocation = new Location(null, 1, 1, 1);

        DamageSource damageSource = mock(DamageSource.class);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);

        return new ItemEffectContext(collisionResult, damageSource, effectSource, triggerTarget, initiationLocation);
    }
}
