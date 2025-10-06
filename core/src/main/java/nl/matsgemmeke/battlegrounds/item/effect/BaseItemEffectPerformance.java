package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseItemEffectPerformance implements ItemEffectPerformance {

    protected final Set<TriggerRun> triggerRuns;
    protected ItemEffectContext currentContext;

    public BaseItemEffectPerformance() {
        this.triggerRuns = new HashSet<>();
    }

    @Override
    public void addTriggerRun(TriggerRun triggerRun) {
        triggerRuns.add(triggerRun);
    }

    @Override
    public void changeSource(ItemEffectSource source) {
        Entity entity = currentContext.getEntity();
        Location initiationLocation = currentContext.getInitiationLocation();

        currentContext = new ItemEffectContext(entity, source, initiationLocation);
    }

    @Override
    public void start(ItemEffectContext context) {
        currentContext = context;
        this.perform(context);
    }

    public abstract void perform(ItemEffectContext context);
}
