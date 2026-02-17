package nl.matsgemmeke.battlegrounds.item.shoot.launcher.adapter;

import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;

public class SimpleTriggerResultAdapter implements TriggerResultAdapter<SimpleTriggerResult> {

    @Override
    public CollisionResult adapt(SimpleTriggerResult triggerResult) {
        return new CollisionResult(null, null, null);
    }
}
