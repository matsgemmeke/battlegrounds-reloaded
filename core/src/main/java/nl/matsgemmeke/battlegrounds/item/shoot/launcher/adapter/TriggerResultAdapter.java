package nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter;

import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;

/**
 * Helps convert {@link TriggerResult} instances into {@link CollisionResult} instances.
 */
public interface TriggerResultAdapter<T extends TriggerResult> {

    CollisionResult adapt(T triggerResult);
}
