package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DamageEffectNewTest {

    private static final DamageProperties PROPERTIES = new DamageProperties(null, null);
    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final ItemEffectContext CONTEXT = createContext();

    @Mock
    private DamageEffectPerformanceFactory damageEffectPerformanceFactory;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;

    private DamageEffectNew damageEffect;

    @BeforeEach
    void setUp() {
        damageEffect = new DamageEffectNew(damageEffectPerformanceFactory, gameContextProvider, GAME_KEY, gameScope);
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
        triggerObserverCaptor.getValue().onActivate();

        TriggerContext triggerContext = triggerContextCaptor.getValue();
        assertThat(triggerContext.entity()).isEqualTo(CONTEXT.getEntity());
        assertThat(triggerContext.target()).isEqualTo(CONTEXT.getSource());

        verify(triggerRun).start();
        verify(performance).addTriggerRun(triggerRun);
        verify(performance).perform(CONTEXT);
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
        verify(performance).perform(CONTEXT);
    }

    @Test
    void undoPerformancesCancelsOngoingPerformances() {
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
        damageEffect.undoPerformances();

        verify(performancePerforming).cancel();
        verify(performanceNotPerforming, never()).cancel();
    }

    private static ItemEffectContext createContext() {
        Entity entity = mock(Entity.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        Location initiationLocation = new Location(null, 1, 1, 1);

        return new ItemEffectContext(entity, source, initiationLocation);
    }
}
