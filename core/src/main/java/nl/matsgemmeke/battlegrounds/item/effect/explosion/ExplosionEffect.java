package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ExplosionEffect extends BaseItemEffect {

    @NotNull
    private DamageProcessor damageProcessor;
    @NotNull
    private ExplosionProperties properties;
    @NotNull
    private RangeProfile rangeProfile;
    @NotNull
    private TargetFinder targetFinder;

    public ExplosionEffect(
            @NotNull ItemEffectActivation effectActivation,
            @NotNull ExplosionProperties properties,
            @NotNull DamageProcessor damageProcessor,
            @NotNull RangeProfile rangeProfile,
            @NotNull TargetFinder targetFinder
    ) {
        super(effectActivation);
        this.properties = properties;
        this.damageProcessor = damageProcessor;
        this.targetFinder = targetFinder;
        this.rangeProfile = rangeProfile;
    }

    public void perform(@NotNull ItemEffectContext context) {
        ItemHolder holder = context.getHolder();
        EffectSource source = context.getSource();
        Location sourceLocation = source.getLocation();
        World world = source.getWorld();
        Entity damageSource = holder.getEntity();

        for (GameEntity target : targetFinder.findTargets(holder, sourceLocation, rangeProfile.getLongRangeDistance())) {
            Location targetLocation = target.getEntity().getLocation();
            Damage damage = this.getDamageForTargetLocation(sourceLocation, targetLocation);

            target.damage(damage);
        }

        for (DeploymentObject deploymentObject : targetFinder.findDeploymentObjects(holder, sourceLocation, rangeProfile.getLongRangeDistance())) {
            if (deploymentObject != source) {
                Location objectLocation = deploymentObject.getLocation();
                Damage damage = this.getDamageForTargetLocation(sourceLocation, objectLocation);

                damageProcessor.processDeploymentObjectDamage(deploymentObject, damage);
            }
        }

        // Remove the source before creating the explosion to prevent calling an extra EntityDamageByEntityEvent
        source.remove();

        world.createExplosion(sourceLocation, properties.power(), properties.setFire(), properties.breakBlocks(), damageSource);
    }

    @NotNull
    private Damage getDamageForTargetLocation(@NotNull Location sourceLocation, @NotNull Location targetLocation) {
        double distance = sourceLocation.distance(targetLocation);
        double damageAmount = rangeProfile.getDamageByDistance(distance);

        return new Damage(damageAmount, DamageType.EXPLOSIVE_DAMAGE);
    }
}
