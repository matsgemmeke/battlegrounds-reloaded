package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;

public abstract class BaseItemEffectPerformance implements ItemEffectPerformance {

    protected ItemEffectContext currentContext;

    @Override
    public void changeActor(Actor actor) {
        currentContext.setActor(actor);
    }

    @Override
    public void setContext(ItemEffectContext context) {
        this.currentContext = context;
    }

    /**
     * By default, this method is a no-op, meaning the effect performance has no side effects. Override this method to
     * implement specific rollback logic for the effect performance implementation.
     */
    @Override
    public void rollback() {
    }
}
