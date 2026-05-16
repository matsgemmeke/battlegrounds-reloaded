package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import org.bukkit.Location;

public class DamageEffectPerformance extends BaseItemEffectPerformance {

    private final DamageProcessor damageProcessor;
    private final DamageProperties properties;

    @Inject
    public DamageEffectPerformance(DamageProcessor damageProcessor, @Assisted DamageProperties properties) {
        this.damageProcessor = damageProcessor;
        this.properties = properties;
    }

    @Override
    public boolean isPerforming() {
        // A damage effect is instant, therefore this effect will never perform for a longer period of time
        return false;
    }

    @Override
    public void start() {
        CollisionResult collisionResult = currentContext.getCollisionResult();
        DamageTarget hitTarget = collisionResult.getHitTarget().orElse(null);
        Location hitLocation = collisionResult.getHitLocation().orElse(null);

        if (hitTarget == null || hitLocation == null) {
            return;
        }

        Location startingLocation = currentContext.getStartingLocation();
        DamageSource damageSource = currentContext.getDamageSource();
        Damage damage = this.createDamage(hitTarget, hitLocation, startingLocation);
        DamageContext damageContext = new DamageContext(damageSource, hitTarget, damage);

        damageProcessor.processDamage(damageContext);

        if (currentContext.getActor() instanceof Removable removableActor) {
            removableActor.remove();
        }
    }

    private Damage createDamage(DamageTarget target, Location hitLocation, Location startingLocation) {
        Hitbox hitbox = target.getHitbox();
        HitboxComponentType hitboxComponentType = hitbox.getIntersectedHitboxComponent(hitLocation)
                .map(HitboxComponent::type)
                .orElse(HitboxComponentType.LIMBS);

        double damageMultiplier = this.getHitboxDamageMultiplier(hitboxComponentType);
        double distance = hitLocation.distance(startingLocation);
        double distanceDamageAmount = properties.rangeProfile().getDamageByDistance(distance);
        double totalDamageAmount = distanceDamageAmount * damageMultiplier;

        DamageType damageType = properties.damageType();

        return new Damage(totalDamageAmount, damageType, hitboxComponentType);
    }

    private double getHitboxDamageMultiplier(HitboxComponentType hitboxComponentType) {
        return switch (hitboxComponentType) {
            case HEAD -> properties.hitboxMultiplierProfile().headshotDamageMultiplier();
            case TORSO -> properties.hitboxMultiplierProfile().bodyDamageMultiplier();
            case LIMBS -> properties.hitboxMultiplierProfile().legsDamageMultiplier();
        };
    }
}
