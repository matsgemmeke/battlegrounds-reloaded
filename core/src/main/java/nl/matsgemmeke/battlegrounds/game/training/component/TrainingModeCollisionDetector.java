package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.TrainingModeEntity;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainingModeCollisionDetector implements CollisionDetector {

    @NotNull
    private BlockCollisionChecker blockCollisionChecker;
    @NotNull
    private EntityStorage<GamePlayer> playerStorage;

    public TrainingModeCollisionDetector(@NotNull BlockCollisionChecker blockCollisionChecker, @NotNull EntityStorage<GamePlayer> playerStorage) {
        this.blockCollisionChecker = blockCollisionChecker;
        this.playerStorage = playerStorage;
    }

    @NotNull
    public List<GameEntity> findTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range) {
        if (location.getWorld() == null) {
            return Collections.emptyList();
        }

        List<GameEntity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            GamePlayer gamePlayer = playerStorage.getEntity(entity);

            if (gamePlayer != null && !gamePlayer.isPassive()) {
                entities.add(gamePlayer);
            }

            if (entity.getType() != EntityType.PLAYER && entity instanceof LivingEntity) {
                entities.add(new TrainingModeEntity((LivingEntity) entity));
            }
        }

        return entities;
    }

    public boolean producesBlockCollisionAt(@NotNull Location location) {
        return blockCollisionChecker.isSolid(location.getBlock(), location);
    }
}
