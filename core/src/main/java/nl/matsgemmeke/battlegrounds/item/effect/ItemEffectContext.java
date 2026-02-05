package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import org.bukkit.Location;

public class ItemEffectContext {

    private final CollisionResult collisionResult;
    private final DamageSource damageSource;
    private final Location startingLocation;
    private Actor actor;

    public ItemEffectContext(CollisionResult collisionResult, DamageSource damageSource, Actor actor, Location startingLocation) {
        this.collisionResult = collisionResult;
        this.damageSource = damageSource;
        this.actor = actor;
        this.startingLocation = startingLocation;
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

    public Location getStartingLocation() {
        return startingLocation;
    }
}
