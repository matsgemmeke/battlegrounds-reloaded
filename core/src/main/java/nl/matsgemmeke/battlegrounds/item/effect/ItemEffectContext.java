package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;

public class ItemEffectContext {

    private final DamageSource damageSource;
    private final Location initiationLocation;
    private Actor actor;
    private CollisionResult collisionResult;
    private ItemEffectSource effectSource;
    private TriggerTarget triggerTarget;

    public ItemEffectContext(DamageSource damageSource, ItemEffectSource effectSource, TriggerTarget triggerTarget, Location initiationLocation) {
        this.damageSource = damageSource;
        this.effectSource = effectSource;
        this.triggerTarget = triggerTarget;
        this.initiationLocation = initiationLocation;
    }

    public ItemEffectContext(CollisionResult collisionResult, DamageSource damageSource, Actor actor, TriggerTarget triggerTarget, Location initiationLocation) {
        this.collisionResult = collisionResult;
        this.damageSource = damageSource;
        this.actor = actor;
        this.triggerTarget = triggerTarget;
        this.initiationLocation = initiationLocation;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public CollisionResult getCollisionResult() {
        return collisionResult;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public ItemEffectSource getEffectSource() {
        return effectSource;
    }

    public void setEffectSource(ItemEffectSource effectSource) {
        this.effectSource = effectSource;
    }

    public Location getInitiationLocation() {
        return initiationLocation;
    }

    public TriggerTarget getTriggerTarget() {
        return triggerTarget;
    }
}
