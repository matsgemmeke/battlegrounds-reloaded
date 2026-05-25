package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import org.bukkit.Location;

public class ItemEffectContext {

    private final CollisionResult collisionResult;
    private final DamageSource damageSource;
    private final Location startingLocation;
    private final String itemName;
    private Actor actor;

    public ItemEffectContext(String itemName, CollisionResult collisionResult, DamageSource damageSource, Location startingLocation, Actor actor) {
        this.itemName = itemName;
        this.collisionResult = collisionResult;
        this.damageSource = damageSource;
        this.startingLocation = startingLocation;
        this.actor = actor;
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

    public String getItemName() {
        return itemName;
    }

    public Location getStartingLocation() {
        return startingLocation;
    }
}
