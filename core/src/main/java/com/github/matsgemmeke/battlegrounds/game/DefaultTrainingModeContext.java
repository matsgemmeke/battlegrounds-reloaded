package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.GameEntity;
import com.github.matsgemmeke.battlegrounds.entity.TrainingModeEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultTrainingModeContext extends AbstractGameContext {

    public DefaultTrainingModeContext(@NotNull BlockCollisionChecker collisionChecker) {
        super(collisionChecker);
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
}
