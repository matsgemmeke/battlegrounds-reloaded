package nl.matsgemmeke.battlegrounds.game.openmode.component;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.OpenModeEntity;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.TargetType;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.LivingEntityRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.Target;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OpenModeTargetFinder implements TargetFinder {

    private final DeploymentInfoProvider deploymentInfoProvider;
    private final HitboxResolver hitboxResolver;
    private final LivingEntityRegistry livingEntityRegistry;
    private final PlayerRegistry playerRegistry;

    @Inject
    public OpenModeTargetFinder(DeploymentInfoProvider deploymentInfoProvider, HitboxResolver hitboxResolver, LivingEntityRegistry livingEntityRegistry, PlayerRegistry playerRegistry) {
        this.deploymentInfoProvider = deploymentInfoProvider;
        this.hitboxResolver = hitboxResolver;
        this.livingEntityRegistry = livingEntityRegistry;
        this.playerRegistry = playerRegistry;
    }

    public boolean containsTargets(TargetQuery query) {
        return !this.findTargets(query).isEmpty();
    }

    @NotNull
    public List<DeploymentObject> findDeploymentObjects(@NotNull UUID entityId, @NotNull Location location, double range) {
        List<DeploymentObject> deploymentObjects = new ArrayList<>();

        for (DeploymentObject deploymentObject : deploymentInfoProvider.getAllDeploymentObjects()) {
            double distance = location.distanceSquared(deploymentObject.getLocation());

            if (distance <= range) {
                deploymentObjects.add(deploymentObject);
            }
        }

        return deploymentObjects;
    }

    @NotNull
    public List<GameEntity> findEnemyTargets(@NotNull UUID entityId, @NotNull Location location, double range) {
        Collection<Entity> entities = this.findTargetEntities(location, range);
        List<GameEntity> targets = new ArrayList<>();

        for (Entity entity : entities) {
            GamePlayer gamePlayer = playerRegistry.findByUniqueId(entity.getUniqueId()).orElse(null);

            if (gamePlayer != null && gamePlayer.getUniqueId().equals(entityId)) {
                continue;
            }

            if (gamePlayer != null && !gamePlayer.isPassive()) {
                targets.add(gamePlayer);
                continue;
            }

            if (entity.getType() != EntityType.PLAYER && entity instanceof LivingEntity livingEntity) {
                // TODO handle optional
                HitboxProvider hitboxProvider = hitboxResolver.resolveHitboxProvider(entity).orElse(null);
                GameEntity target = new OpenModeEntity(livingEntity, hitboxProvider);

                targets.add(target);
            }
        }

        return targets;
    }

    public List<Target> findTargets(TargetQuery query) {
        List<Target> targets = new ArrayList<>();
        Location location = query.getLocation().orElseThrow(() -> new IllegalArgumentException("No location provided"));
        World world = Optional.ofNullable(location.getWorld()).orElseThrow(() -> new IllegalArgumentException("Provided location has no world"));

        Optional<UUID> uniqueId = query.getUniqueId();
        Optional<Double> entityFindingRange = query.getRange(TargetType.ENTITY);
        Optional<Double> deploymentObjectFindingRange = query.getRange(TargetType.DEPLOYMENT_OBJECT);
        boolean enemiesOnly = query.isEnemiesOnly().orElse(false);

        entityFindingRange.ifPresent(range -> {
            playerRegistry.getAll().stream()
                    .filter(gamePlayer -> !enemiesOnly || uniqueId.map(id -> !id.equals(gamePlayer.getUniqueId())).orElse(true))
                    .filter(gamePlayer -> gamePlayer.getLocation().distanceSquared(location) <= range)
                    .forEach(targets::add);

            world.getNearbyEntities(location, range, range, range).stream()
                    .filter(entity -> entity.getType() != EntityType.PLAYER)
                    .filter(LivingEntity.class::isInstance)
                    .map(entity -> livingEntityRegistry.register((LivingEntity) entity))
                    .filter(gameEntity -> gameEntity.getHitbox().intersects(location))
                    .forEach(targets::add);
        });
        deploymentObjectFindingRange.ifPresent(range -> {
            deploymentInfoProvider.getAllDeploymentObjects().stream()
                    .filter(deploymentObject -> !enemiesOnly || uniqueId.map(id -> !id.equals(deploymentObject.getUniqueId())).orElse(true))
                    .filter(deploymentObject -> deploymentObject.getLocation().distanceSquared(location) <= range)
                    .forEach(targets::add);
        });

        return targets;
    }

    @NotNull
    public List<GameEntity> findTargets(@NotNull UUID entityId, @NotNull Location location, double range) {
        Collection<Entity> entities = this.findTargetEntities(location, range);
        List<GameEntity> targets = new ArrayList<>();

        for (Entity entity : entities) {
            GamePlayer gamePlayer = playerRegistry.findByUniqueId(entity.getUniqueId()).orElse(null);

            if (gamePlayer != null && !gamePlayer.isPassive()) {
                targets.add(gamePlayer);
                continue;
            }

            if (entity.getType() != EntityType.PLAYER && entity instanceof LivingEntity livingEntity) {
                // TODO handle optional
                HitboxProvider hitboxProvider = hitboxResolver.resolveHitboxProvider(entity).orElse(null);
                GameEntity target = new OpenModeEntity(livingEntity, hitboxProvider);

                targets.add(target);
            }
        }

        return targets;
    }

    @NotNull
    private Collection<Entity> findTargetEntities(@NotNull Location location, double range) {
        World world = location.getWorld();

        if (world == null) {
            return Collections.emptyList();
        }

        return world.getNearbyEntities(location, range, range, range);
    }
}
