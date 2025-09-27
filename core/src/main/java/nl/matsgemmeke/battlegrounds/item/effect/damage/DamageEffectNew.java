package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class DamageEffectNew implements ItemEffectNew {

    @NotNull
    private final DamageEffectPerformanceFactory damageEffectPerformanceFactory;
    @NotNull
    private final Set<ItemEffectPerformance> performances;
    @NotNull
    private final Set<TriggerExecutor> triggerExecutors;
    private DamageType damageType;
    private RangeProfile rangeProfile;

    @Inject
    public DamageEffectNew(@NotNull DamageEffectPerformanceFactory damageEffectPerformanceFactory) {
        this.damageEffectPerformanceFactory = damageEffectPerformanceFactory;
        this.performances = new HashSet<>();
        this.triggerExecutors = new HashSet<>();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public void setRangeProfile(RangeProfile rangeProfile) {
        this.rangeProfile = rangeProfile;
    }

    public ItemEffectPerformance perform(ItemEffectContext context) {
        DamageProperties properties = new DamageProperties(rangeProfile, damageType);

        ItemEffectPerformance performance = damageEffectPerformanceFactory.create(properties);

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerContext triggerContext = new TriggerContext(context.getEntity(), context.getSource());
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);

            performance.addTriggerRun(triggerRun);
        }

        return performance;
    }

    public void undoPerformances() {
        performances.forEach(ItemEffectPerformance::cancel);
        performances.clear();
    }
}
