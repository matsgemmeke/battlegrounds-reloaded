package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.BaseGameContext;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class DefaultSessionContext extends BaseGameContext {

    public DefaultSessionContext(@NotNull BlockCollisionChecker collisionChecker) {
        super(collisionChecker);
    }

    @NotNull
    public Collection<GameEntity> getTargets(@NotNull GameEntity gameEntity, @NotNull Location location, double range) {
        return Collections.emptyList();
    }
}
