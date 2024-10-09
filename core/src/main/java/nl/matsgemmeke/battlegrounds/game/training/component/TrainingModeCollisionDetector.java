package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class TrainingModeCollisionDetector implements CollisionDetector {

    @NotNull
    private BlockCollisionChecker blockCollisionChecker;

    public TrainingModeCollisionDetector(@NotNull BlockCollisionChecker blockCollisionChecker) {
        this.blockCollisionChecker = blockCollisionChecker;
    }

    public boolean producesBlockCollisionAt(@NotNull Location location) {
        return blockCollisionChecker.isSolid(location.getBlock(), location);
    }
}
