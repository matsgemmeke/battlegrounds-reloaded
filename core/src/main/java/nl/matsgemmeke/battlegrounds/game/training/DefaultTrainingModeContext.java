package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.TrainingModeEntity;
import nl.matsgemmeke.battlegrounds.game.BaseGameContext;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultTrainingModeContext extends BaseGameContext {

    @NotNull
    private TrainingMode trainingMode;

    public DefaultTrainingModeContext(
            @NotNull TrainingMode trainingMode,
            @NotNull BlockCollisionChecker collisionChecker
    ) {
        super(collisionChecker);
        this.trainingMode = trainingMode;
    }

    @NotNull
    public Collection<GameEntity> getTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range) {
        if (location.getWorld() == null) {
            return Collections.emptyList();
        }

        List<GameEntity> entities = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            if (entity != gameEntity.getEntity() && entity instanceof LivingEntity) {
                entities.add(new TrainingModeEntity((LivingEntity) entity));
            }
        }

        return entities;
    }

    @NotNull
    public GameItem registerItem(@NotNull Item item) {
        return trainingMode.addItem(item);
    }
}
