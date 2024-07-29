package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.TrainingModeEntity;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TrainingModeCollisionDetector implements CollisionDetector {

    private BlockCollisionChecker blockCollisionChecker;

    public TrainingModeCollisionDetector(@NotNull BlockCollisionChecker blockCollisionChecker) {
        this.blockCollisionChecker = blockCollisionChecker;
    }

    @NotNull
    public Collection<GameEntity> findTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range) {
        if (location.getWorld() == null) {
            return Collections.emptyList();
        }

        List<GameEntity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            if (entity instanceof LivingEntity) {
                entities.add(new TrainingModeEntity((LivingEntity) entity));
            }
        }

        return entities;
    }

    public boolean producesBlockCollisionAt(@NotNull Location location) {
        return blockCollisionChecker.isSolid(location.getBlock(), location);
    }
}
