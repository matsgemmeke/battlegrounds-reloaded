package nl.matsgemmeke.battlegrounds.item.effect.explosion;

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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExplosionEffectNewTest {

    private static final ExplosionProperties PROPERTIES = new ExplosionProperties(null, 2.0f, false, false);
    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final ItemEffectContext CONTEXT = createContext();

    @Mock
    private ExplosionEffectPerformanceFactory explosionEffectPerformanceFactory;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;

    private ExplosionEffectNew explosionEffect;
    
    @BeforeEach
    void setUp() {
        explosionEffect = new ExplosionEffectNew(explosionEffectPerformanceFactory, gameContextProvider, GAME_KEY, gameScope);
    }

    @Test
    void startThrowsItemEffectPerformanceExceptionWhenPropertiesAreNotSet() {
        assertThatThrownBy(() -> explosionEffect.start(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform explosion effect: properties not set");
    }

    @Test
    void startThrowsItemEffectPerformanceExceptionWhenThereIsNoGameContext() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        explosionEffect.setProperties(PROPERTIES);

        assertThatThrownBy(() -> explosionEffect.start(CONTEXT))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform explosion effect: no game context for game key OPEN-MODE can be found");
    }

    @Test
    void startCreatesAndStartsTriggerRunsWithObserversThatStartPerformance() {
        ExplosionEffectPerformance performance = mock(ExplosionEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);
        TriggerRun triggerRun = mock(TriggerRun.class);

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        when(explosionEffectPerformanceFactory.create(PROPERTIES)).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        explosionEffect.addTriggerExecutor(triggerExecutor);
        explosionEffect.setProperties(PROPERTIES);
        explosionEffect.start(CONTEXT);

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
    void startCreatesAndStartsPerformanceWhenNoTriggerExecutorsAreAdded() {
        ExplosionEffectPerformance performance = mock(ExplosionEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);

        when(explosionEffectPerformanceFactory.create(PROPERTIES)).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        explosionEffect.setProperties(PROPERTIES);
        explosionEffect.start(CONTEXT);

        verify(performance, never()).addTriggerRun(any(TriggerRun.class));
        verify(performance).perform(CONTEXT);
    }

    private static ItemEffectContext createContext() {
        Entity entity = mock(Entity.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        Location initiationLocation = new Location(null, 1, 1, 1);

        return new ItemEffectContext(entity, source, initiationLocation);
    }
}
