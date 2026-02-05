package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;

public class DamageEffectPerformance extends BaseItemEffectPerformance {

    // We use this multiplier when we try to damage a target, but the origin location falls outside the target's hitbox
    private static final double DEFAULT_DAMAGE_MULTIPLIER = 1.0;

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
    public void perform(ItemEffectContext context) {
        CollisionResult collisionResult = context.getCollisionResult();
        DamageTarget hitTarget = collisionResult.getHitTarget().orElse(null);
        Location hitLocation = collisionResult.getHitLocation().orElse(null);

        if (hitTarget == null || hitLocation == null) {
            return;
        }

        Location startingLocation = context.getStartingLocation();
        DamageSource damageSource = context.getDamageSource();
        Damage damage = this.createDamage(hitTarget, hitLocation, startingLocation);
        DamageContext damageContext = new DamageContext(damageSource, hitTarget, damage);

        damageProcessor.processDamage(damageContext);
    }

    private Damage createDamage(DamageTarget target, Location hitLocation, Location startingLocation) {
        Hitbox hitbox = target.getHitbox();
        double damageMultiplier = this.getHitboxDamageMultiplier(hitbox, hitLocation);
        double distance = hitLocation.distance(startingLocation);
        double distanceDamageAmount = properties.rangeProfile().getDamageByDistance(distance);
        double totalDamageAmount = distanceDamageAmount * damageMultiplier;

        DamageType damageType = properties.damageType();

        return new Damage(totalDamageAmount, damageType);
    }

    private double getHitboxDamageMultiplier(Hitbox hitbox, Location hitLocation) {
        HitboxComponent hitboxComponent = hitbox.getIntersectedHitboxComponent(hitLocation).orElse(null);

        if (hitboxComponent == null) {
            return DEFAULT_DAMAGE_MULTIPLIER;
        }

        return switch (hitboxComponent.type()) {
            case HEAD -> properties.hitboxMultiplierProfile().headshotDamageMultiplier();
            case TORSO -> properties.hitboxMultiplierProfile().bodyDamageMultiplier();
            case LIMBS -> properties.hitboxMultiplierProfile().legsDamageMultiplier();
        };
    }
}
