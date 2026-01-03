package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.HitboxTargetCondition;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.ProximityTargetCondition;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.TargetCondition;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class DamageEffectPerformance extends BaseItemEffectPerformance {

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
        DamageSource damageSource = context.getDamageSource();
        UUID damageSourceUniqueId = damageSource.getUniqueId();
        Location effectSourceLocation = context.getEffectSource().getLocation();
        Collection<TargetCondition> targetConditions = this.getTargetConditions();

        TargetQuery query = new TargetQuery()
                .uniqueId(damageSourceUniqueId)
                .location(effectSourceLocation)
                .conditions(targetConditions)
                .enemiesOnly(true);

        for (DamageTarget target : targetFinder.findTargets(query)) {
            Location targetLocation = target.getLocation();

            Damage damage = this.createDamage(target, effectSourceLocation, targetLocation);
            DamageContext damageContext = new DamageContext(damageSource, target, damage);

            damageProcessor.processDamage(damageContext);
        }
    }

    private Damage createDamage(DamageTarget target, Location effectSourceLocation, Location targetLocation) {
        Hitbox hitbox = target.getHitbox();
        double damageMultiplier = this.getHitboxDamageMultiplier(hitbox, effectSourceLocation).orElse(0.0);
        double distance = effectSourceLocation.distance(targetLocation);
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

    private Collection<TargetCondition> getTargetConditions() {
        double radius = properties.radius();

        if (radius > 0.0) {
            return Set.of(new ProximityTargetCondition(radius));
        } else {
            return Set.of(new HitboxTargetCondition());
        }
    }
}
