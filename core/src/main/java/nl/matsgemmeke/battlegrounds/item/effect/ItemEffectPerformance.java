package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;

public interface ItemEffectPerformance {

    void addTriggerRun(TriggerRun triggerRun);

    void changeActor(Actor actor);

    boolean isPerforming();

    void setContext(ItemEffectContext context);

    void start();

    void cancel();

    void rollback();
}
