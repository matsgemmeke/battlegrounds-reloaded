package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.BaseEffect;
import nl.matsgemmeke.battlegrounds.item.effect.EffectContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DamageEffect extends BaseEffect {

    private static final double DEPLOYMENT_OBJECT_FINDING_RANGE = 0.3;
    private static final double ENTITY_FINDING_RANGE = 0.1;

    @NotNull
    private final DamageProcessor damageProcessor;
    @NotNull
    private final DamageProperties properties;
    @NotNull
    private final TargetFinder targetFinder;

    public DamageEffect(@NotNull DamageProperties properties, @NotNull DamageProcessor damageProcessor, @NotNull TargetFinder targetFinder) {
        this.properties = properties;
        this.damageProcessor = damageProcessor;
        this.targetFinder = targetFinder;
    }

    public void perform(@NotNull EffectContext context) {
        Entity entity = context.getEntity();
        UUID entityId = entity.getUniqueId();
        Location initiationLocation = context.getInitiationLocation();
        Location sourceLocation = context.getSource().getLocation();

        for (GameEntity target : targetFinder.findEnemyTargets(entityId, sourceLocation, ENTITY_FINDING_RANGE)) {
            Location targetLocation = target.getEntity().getLocation();

            Damage damage = this.createDamage(initiationLocation, targetLocation);
            target.damage(damage);
        }

        for (DeploymentObject deploymentObject : targetFinder.findDeploymentObjects(entityId, sourceLocation, DEPLOYMENT_OBJECT_FINDING_RANGE)) {
            Location objectLocation = deploymentObject.getLocation();

            Damage damage = this.createDamage(initiationLocation, objectLocation);
            damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);
        }
    }

    @NotNull
    private Damage createDamage(@NotNull Location originLocation, @NotNull Location targetLocation) {
        double distance = originLocation.distance(targetLocation);
        double damageAmount = properties.rangeProfile().getDamageByDistance(distance);
        DamageType damageType = properties.damageType();

        return new Damage(damageAmount, damageType);
    }
}
