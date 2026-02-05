package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.bukkit.Location;

public class ItemEffectContext {

    private final CollisionResult collisionResult;
    private final DamageSource damageSource;
    private final Location initiationLocation;
    private Actor actor;
    private ItemEffectSource effectSource;

    public ItemEffectContext(CollisionResult collisionResult, DamageSource damageSource, Actor actor, Location initiationLocation) {
        this.collisionResult = collisionResult;
        this.damageSource = damageSource;
        this.actor = actor;
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
}
