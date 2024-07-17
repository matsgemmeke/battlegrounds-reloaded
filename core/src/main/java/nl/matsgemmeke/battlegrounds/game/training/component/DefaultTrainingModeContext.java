package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.GameContext;
import org.jetbrains.annotations.NotNull;

public class DefaultTrainingModeContext implements GameContext {

    @NotNull
    public CollisionDetector getCollisionDetector() {
        BlockCollisionChecker blockCollisionChecker = new BlockCollisionChecker();

        return new TrainingModeCollisionDetector(blockCollisionChecker);
    }
}
