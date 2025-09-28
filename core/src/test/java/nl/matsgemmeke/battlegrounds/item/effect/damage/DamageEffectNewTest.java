package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
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

    private static final DamageType DAMAGE_TYPE = DamageType.BULLET_DAMAGE;
    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final RangeProfile RANGE_PROFILE = new RangeProfile(30.0, 10.0, 20.0, 20.0, 10.0, 30.0);

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
    void startThrowsItemEffectPerformanceExceptionWhenDamageTypeIsNotSet() {
        ItemEffectContext context = this.createContext();

        assertThatThrownBy(() -> damageEffect.start(context))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform damage effect: Required variable 'damageType' is not provided");
    }

    @Test
    void startThrowsItemEffectPerformanceExceptionWhenRangeProfileIsNotSet() {
        ItemEffectContext context = this.createContext();

        damageEffect.setDamageType(DAMAGE_TYPE);

        assertThatThrownBy(() -> damageEffect.start(context))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform damage effect: Required variable 'rangeProfile' is not provided");
    }

    @Test
    void startThrowsItemEffectPerformanceExceptionWhenThereIsNoGameContext() {
        ItemEffectContext context = this.createContext();

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        damageEffect.setDamageType(DAMAGE_TYPE);
        damageEffect.setRangeProfile(RANGE_PROFILE);

        assertThatThrownBy(() -> damageEffect.start(context))
                .isInstanceOf(ItemEffectPerformanceException.class)
                .hasMessage("Unable to perform damage effect: No game context for game key OPEN-MODE can be found");
    }

    @Test
    void startCreatesAndStartsNewDamageEffectPerformance() {
        DamageEffectPerformance performance = mock(DamageEffectPerformance.class);
        GameContext gameContext = mock(GameContext.class);
        ItemEffectContext context = this.createContext();
        TriggerRun triggerRun = mock(TriggerRun.class);

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        when(damageEffectPerformanceFactory.create(any(DamageProperties.class))).thenReturn(performance);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(gameScope.supplyInScope(eq(gameContext), any())).thenAnswer(invocation -> {
            Supplier<ItemEffectPerformance> performanceSupplier = invocation.getArgument(1);
            return performanceSupplier.get();
        });

        damageEffect.setDamageType(DAMAGE_TYPE);
        damageEffect.setRangeProfile(RANGE_PROFILE);
        damageEffect.addTriggerExecutor(triggerExecutor);
        damageEffect.start(context);

        ArgumentCaptor<DamageProperties> damagePropertiesCaptor = ArgumentCaptor.forClass(DamageProperties.class);
        verify(damageEffectPerformanceFactory).create(damagePropertiesCaptor.capture());

        ArgumentCaptor<TriggerContext> triggerContextCaptor = ArgumentCaptor.forClass(TriggerContext.class);
        verify(triggerExecutor).createTriggerRun(triggerContextCaptor.capture());

        DamageProperties damageProperties = damagePropertiesCaptor.getValue();
        assertThat(damageProperties.damageType()).isEqualTo(DAMAGE_TYPE);
        assertThat(damageProperties.rangeProfile()).isEqualTo(RANGE_PROFILE);

        TriggerContext triggerContext = triggerContextCaptor.getValue();
        assertThat(triggerContext.entity()).isEqualTo(context.getEntity());
        assertThat(triggerContext.target()).isEqualTo(context.getSource());

        verify(performance).addTriggerRun(triggerRun);
        verify(performance).perform(context);
    }

    @Test
    void undoPerformancesCancelsOngoingPerformances() {
        GameContext gameContext = mock(GameContext.class);
        ItemEffectContext context = this.createContext();

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

        damageEffect.setDamageType(DAMAGE_TYPE);
        damageEffect.setRangeProfile(RANGE_PROFILE);
        damageEffect.start(context);
        damageEffect.start(context);
        damageEffect.undoPerformances();

        verify(performancePerforming).cancel();
        verify(performanceNotPerforming, never()).cancel();
    }

    private ItemEffectContext createContext() {
        Entity entity = mock(Entity.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        Location initiationLocation = new Location(null, 1, 1, 1);

        return new ItemEffectContext(entity, source, initiationLocation);
    }
}
