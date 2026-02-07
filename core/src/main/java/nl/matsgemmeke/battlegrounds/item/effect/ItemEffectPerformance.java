package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;

public interface ItemEffectPerformance {

    void changeActor(Actor actor);

    boolean isPerforming();

    void setContext(ItemEffectContext context);

    void start();

    void cancel();

    void rollback();
}
