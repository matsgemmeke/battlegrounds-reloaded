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
        DamageTarget damageTarget = collisionResult.getHitTarget().orElse(null);
        Location hitLocation = collisionResult.getHitLocation().orElse(null);

        if (damageTarget == null || hitLocation == null) {
            return;
        }

        Location startingLocation = currentContext.getStartingLocation();
        DamageSource damageSource = currentContext.getDamageSource();
        double distance = startingLocation.distance(hitLocation);

        Hitbox hitbox = damageTarget.getHitbox();
        HitboxComponentType hitboxComponentType = hitbox.getIntersectedHitboxComponent(hitLocation)
                .map(HitboxComponent::type)
                .orElse(HitboxComponentType.LIMBS);

        double distanceDamageAmount = properties.rangeProfile().getDamageByDistance(distance);
        double hitboxDamageModifier = this.getHitboxDamageModifier(hitboxComponentType);
        double totalDamageAmount = distanceDamageAmount * hitboxDamageModifier;

        DamageType damageType = properties.damageType();
        Damage damage = new Damage(totalDamageAmount, damageType, hitboxComponentType);

        DamageContext damageContext = new DamageContext(damageSource, damageTarget, damage, distance);

        damageProcessor.processDamage(damageContext);

        if (currentContext.getActor() instanceof Removable removableActor) {
            removableActor.remove();
        }
    }

    private double getHitboxDamageModifier(HitboxComponentType hitboxComponentType) {
        return switch (hitboxComponentType) {
            case HEAD -> properties.hitboxDamageProfile().headDamageModifier();
            case TORSO -> properties.hitboxDamageProfile().torsoDamageModifier();
            case LIMBS -> properties.hitboxDamageProfile().limbsDamageMultiplier();
        };
    }
}
