package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter.BlockTriggerResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter.DamageTargetTriggerResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter.SimpleTriggerResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter.TriggerResultAdapter;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Util class that maps given {@link TriggerResult} instances and returns a corresponding {@link CollisionResult}.
 */
public class CollisionResultMapper {

    private static final Map<Class<? extends TriggerResult>, TriggerResultAdapter<?>> ADAPTERS = new HashMap<>();

    static {
        ADAPTERS.put(SimpleTriggerResult.class, new SimpleTriggerResultAdapter());
        ADAPTERS.put(BlockTriggerResult.class, new BlockTriggerResultAdapter());
        ADAPTERS.put(DamageTargetTriggerResult.class, new DamageTargetTriggerResultAdapter());
    }

    public CollisionResult map(TriggerResult triggerResult) {
        TriggerResultAdapter<?> adapter = ADAPTERS.get(triggerResult.getClass());

        if (adapter == null) {
            throw new IllegalStateException("No TriggerResultAdapter registered for " + triggerResult.getClass().getSimpleName());
        }

        return adapt(adapter, triggerResult);
    }

    @SuppressWarnings("unchecked")
    private <T extends TriggerResult> CollisionResult adapt(TriggerResultAdapter<T> adapter, TriggerResult triggerResult) {
        return adapter.adapt((T) triggerResult);
    }
}
