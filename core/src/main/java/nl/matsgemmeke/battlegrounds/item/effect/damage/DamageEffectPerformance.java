package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.TargetType;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class DamageEffectPerformance extends BaseItemEffectPerformance {

    private static final double DEPLOYMENT_OBJECT_FINDING_RANGE = 0.2;
    private static final double ENTITY_FINDING_RANGE = 0.1;

    private final DamageProcessor damageProcessor;
    private final DamageProperties properties;
    private final TargetFinder targetFinder;

    @Inject
    public DamageEffectPerformance(DamageProcessor damageProcessor, TargetFinder targetFinder, @Assisted DamageProperties properties) {
        this.damageProcessor = damageProcessor;
        this.targetFinder = targetFinder;
        this.properties = properties;
    }

    @Override
    public boolean isPerforming() {
        // A damage effect is instant, therefore this effect will never perform for a longer period of time
        return false;
    }

    @Override
    public void perform(ItemEffectContext context) {
        Entity entity = context.getEntity();
        UUID entityId = entity.getUniqueId();
        Location sourceLocation = context.getSource().getLocation();

        TargetQuery query = new TargetQuery()
                .uniqueId(entityId)
                .location(sourceLocation)
                .range(TargetType.ENTITY, ENTITY_FINDING_RANGE)
                .range(TargetType.DEPLOYMENT_OBJECT, DEPLOYMENT_OBJECT_FINDING_RANGE)
                .enemiesOnly(true);

        for (DamageTarget target : targetFinder.findTargets(query)) {
            Location targetLocation = target.getLocation();

            Damage damage = this.createDamage(target, sourceLocation, targetLocation);
            DamageContext damageContext = new DamageContext(null, target, damage);

            damageProcessor.processDamage(damageContext);
        }
    }

    private Damage createDamage(DamageTarget target, Location sourceLocation, Location targetLocation) {
        Hitbox hitbox = target.getHitbox();
        double damageMultiplier = this.getHitboxDamageMultiplier(hitbox, sourceLocation).orElse(0.0);
        double distance = sourceLocation.distance(targetLocation);
        double distanceDamageAmount = properties.rangeProfile().getDamageByDistance(distance);
        double totalDamageAmount = distanceDamageAmount * damageMultiplier;

        DamageType damageType = properties.damageType();

        return new Damage(totalDamageAmount, damageType);
    }

    private Optional<Double> getHitboxDamageMultiplier(Hitbox hitbox, Location hitLocation) {
        HitboxComponent hitboxComponent = hitbox.getIntersectedHitboxComponent(hitLocation).orElse(null);

        if (hitboxComponent == null) {
            return Optional.empty();
        }

        return switch (hitboxComponent.type()) {
            case HEAD -> Optional.of(properties.hitboxMultiplierProfile().headshotDamageMultiplier());
            case TORSO -> Optional.of(properties.hitboxMultiplierProfile().bodyDamageMultiplier());
            case LIMBS -> Optional.of(properties.hitboxMultiplierProfile().legsDamageMultiplier());
        };
    }
}
