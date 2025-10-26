package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxPart;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class DamageEffectPerformance extends BaseItemEffectPerformance {

    private static final double DEPLOYMENT_OBJECT_FINDING_RANGE = 0.3;
    private static final double ENTITY_FINDING_RANGE = 0.1;

    private final DamageProcessor damageProcessor;
    private final DamageProperties properties;
    private final HitboxResolver hitboxResolver;
    private final TargetFinder targetFinder;

    @Inject
    public DamageEffectPerformance(DamageProcessor damageProcessor, HitboxResolver hitboxResolver, TargetFinder targetFinder, @Assisted DamageProperties properties) {
        this.damageProcessor = damageProcessor;
        this.hitboxResolver = hitboxResolver;
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
        Location initiationLocation = context.getInitiationLocation();
        Location sourceLocation = context.getSource().getLocation();

        for (GameEntity target : targetFinder.findEnemyTargets(entityId, sourceLocation, ENTITY_FINDING_RANGE)) {
            Entity targetEntity = target.getEntity();
            Location targetLocation = targetEntity.getLocation();

            Damage damage = this.createDamage(targetEntity, initiationLocation, targetLocation);
            target.damage(damage);
        }

        for (DeploymentObject deploymentObject : targetFinder.findDeploymentObjects(entityId, sourceLocation, DEPLOYMENT_OBJECT_FINDING_RANGE)) {
            Location objectLocation = deploymentObject.getLocation();

            Damage damage = this.createDamage(initiationLocation, objectLocation);
            damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);
        }
    }

    private Damage createDamage(Entity entity, Location initiationLocation, Location targetLocation) {
        double damageMultiplier = this.getHitboxDamageMultiplier(entity, initiationLocation);
        double distance = initiationLocation.distance(targetLocation);
        double distanceDamageAmount = properties.rangeProfile().getDamageByDistance(distance);
        double totalDamageAmount = distanceDamageAmount * damageMultiplier;

        DamageType damageType = properties.damageType();

        return new Damage(totalDamageAmount, damageType);
    }

    private Damage createDamage(Location initiationLocation, Location targetLocation) {
        double distance = initiationLocation.distance(targetLocation);
        double damageAmount = properties.rangeProfile().getDamageByDistance(distance);
        DamageType damageType = properties.damageType();

        return new Damage(damageAmount, damageType);
    }

    private double getHitboxDamageMultiplier(Entity entity, Location hitLocation) {
        HitboxPart hitboxPart = hitboxResolver.resolveHitboxPart(entity, hitLocation).orElse(null);

        if (hitboxPart == null) {
            return 0;
        }

        return switch (hitboxPart) {
            case HEAD -> properties.hitboxMultiplierProfile().headshotDamageMultiplier();
            case BODY -> properties.hitboxMultiplierProfile().bodyDamageMultiplier();
            case LEGS -> properties.hitboxMultiplierProfile().legsDamageMultiplier();
        };
    }
}
