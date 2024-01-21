package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class DefaultSessionContext extends AbstractGameContext {

    public DefaultSessionContext(@NotNull BlockCollisionChecker collisionChecker) {
        super(collisionChecker);
    }

    @NotNull
    public Collection<BattleEntity> getTargets(@NotNull BattleEntity battleEntity, @NotNull Location location, double range) {
        return Collections.emptyList();
    }
}
