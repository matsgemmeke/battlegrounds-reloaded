package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.BaseGameContext;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

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
    public GameItem registerItem(@NotNull Item item) {
        return trainingMode.addItem(item);
    }
}
