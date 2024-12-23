package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.TrainingModeEntity;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.storage.DeploymentObjectStorage;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainingModeTargetFinder implements TargetFinder {

    @NotNull
    private DeploymentObjectStorage deploymentObjectStorage;
    @NotNull
    private EntityStorage<GamePlayer> playerStorage;

    public TrainingModeTargetFinder(
            @NotNull DeploymentObjectStorage deploymentObjectStorage,
            @NotNull EntityStorage<GamePlayer> playerStorage
    ) {
        this.deploymentObjectStorage = deploymentObjectStorage;
        this.playerStorage = playerStorage;
    }

    @NotNull
    public List<DeploymentObject> findDeploymentObjects(@NotNull GameEntity gameEntity, @NotNull Location location, double range) {
        List<DeploymentObject> deploymentObjects = new ArrayList<>();

        for (DeploymentObject deploymentObject : deploymentObjectStorage.getDeploymentObjects()) {
            double distance = location.distance(deploymentObject.getLocation());

            if (distance <= range) {
                deploymentObjects.add(deploymentObject);
            }
        }

        return deploymentObjects;
    }

    @NotNull
    public List<GameEntity> findEnemyTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range) {
        List<GameEntity> targets = this.findTargets(gameEntity, location, range);
        // Remove the given entity, since it is not an enemy of itself
        targets.remove(gameEntity);

        return targets;
    }

    @NotNull
    public List<GameEntity> findTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range) {
        World world = location.getWorld();

        if (world == null) {
            return Collections.emptyList();
        }

        List<GameEntity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            GamePlayer gamePlayer = playerStorage.getEntity(entity);

            if (gamePlayer != null && !gamePlayer.isPassive()) {
                entities.add(gamePlayer);
                continue;
            }

            if (entity.getType() != EntityType.PLAYER && entity instanceof LivingEntity) {
                entities.add(new TrainingModeEntity((LivingEntity) entity));
            }
        }

        return entities;
    }
}
