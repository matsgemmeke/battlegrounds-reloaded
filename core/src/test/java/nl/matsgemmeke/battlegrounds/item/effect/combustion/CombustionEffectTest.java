package nl.matsgemmeke.battlegrounds.item.effect.combustion;

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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CombustionEffectTest {

    private static final CombustionProperties PROPERTIES = new CombustionProperties(null, null, 1, 2, 0.1, 5L, 10, 20, false, false);
    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();
    private static final ItemEffectContext CONTEXT = createContext();

    @Mock
    private CombustionEffectPerformanceFactory combustionEffectPerformanceFactory;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;

    private CombustionEffect combustionEffect;

    @BeforeEach
    void setUp() {
        combustionEffect = new CombustionEffect(combustionEffectPerformanceFactory, gameContextProvider, GAME_KEY, gameScope);
    }

    @Test
    void startPerformanceThrowsItemEffectPerformanceExceptionWhenPropertiesAreNotSet() {
        assertThatThrownBy(() -> combustionEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform combustion effect: properties not set");
    }

    @Test
    void startPerformanceThrowsItemEffectPerformanceExceptionWhenThereIsNoGameContext() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        combustionEffect.setProperties(PROPERTIES);

        assertThatThrownBy(() -> combustionEffect.startPerformance(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform combustion effect: no game context for game key OPEN-MODE can be found");
    }

    @Test
    void startPerformanceCreatesAndStartsTriggerRunsWithObserversThatStartPerformance() {
        CombustionEffectPerformance performance = mock(CombustionEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);
        TriggerRun triggerRun = mock(TriggerRun.class);
        TriggerResult triggerResult = mock(TriggerResult.class);

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        when(combustionEffectPerformanceFactory.create(PROPERTIES)).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        combustionEffect.addTriggerExecutor(triggerExecutor);
        combustionEffect.setProperties(PROPERTIES);
        combustionEffect.startPerformance(CONTEXT);

        ArgumentCaptor<TriggerContext> triggerContextCaptor = ArgumentCaptor.forClass(TriggerContext.class);
        verify(triggerExecutor).createTriggerRun(triggerContextCaptor.capture());

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());
        triggerObserverCaptor.getValue().onActivate(triggerResult);

        assertThat(triggerContextCaptor.getValue()).satisfies(triggerContext -> {
            assertThat(triggerContext.sourceId()).isEqualTo(DAMAGE_SOURCE_ID);
            assertThat(triggerContext.target()).isEqualTo(CONTEXT.getTriggerTarget());
        });

        verify(triggerRun).start();
        verify(performance).addTriggerRun(triggerRun);
        verify(performance).setContext(CONTEXT);
        verify(performance).start();
    }

    @Test
    void startPerformanceCreatesAndStartsPerformanceWhenNoTriggerExecutorsAreAdded() {
        CombustionEffectPerformance performance = mock(CombustionEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);

        when(combustionEffectPerformanceFactory.create(PROPERTIES)).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        combustionEffect.setProperties(PROPERTIES);
        combustionEffect.startPerformance(CONTEXT);

        verify(performance, never()).addTriggerRun(any(TriggerRun.class));
        verify(performance).setContext(CONTEXT);
        verify(performance).start();
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
