package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.EntityFinder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TrainingModeCollisionDetector implements CollisionDetector {

    @NotNull
    private BlockCollisionChecker blockCollisionChecker;
    @NotNull
    private EntityFinder entityFinder;

    public TrainingModeCollisionDetector(@NotNull BlockCollisionChecker blockCollisionChecker, @NotNull EntityFinder entityFinder) {
        this.blockCollisionChecker = blockCollisionChecker;
        this.entityFinder = entityFinder;
    }

    @NotNull
    public Collection<GameEntity> findTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range) {
        if (location.getWorld() == null) {
            return Collections.emptyList();
        }

        List<GameEntity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            GameEntity target = entityFinder.findEntity(entity);
            if (target != null) {
                entities.add(target);
            }
        }

        return entities;
    }

    public boolean producesBlockCollisionAt(@NotNull Location location) {
        return blockCollisionChecker.isSolid(location.getBlock(), location);
    }
}
