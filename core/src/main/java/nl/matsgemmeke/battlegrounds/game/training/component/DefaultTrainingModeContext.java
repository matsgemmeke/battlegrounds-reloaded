package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

public class DefaultTrainingModeContext implements GameContext {

    @NotNull
    private TrainingMode trainingMode;

    public DefaultTrainingModeContext(@NotNull TrainingMode trainingMode) {
        this.trainingMode = trainingMode;
    }

    @NotNull
    public AudioEmitter getAudioEmitter() {
        return new DefaultAudioEmitter();
    }

    @NotNull
    public CollisionDetector getCollisionDetector() {
        BlockCollisionChecker blockCollisionChecker = new BlockCollisionChecker();

        return new TrainingModeCollisionDetector(blockCollisionChecker);
    }

    @NotNull
    public EntityRegistry<Item, GameItem> getItemRegistry() {
        return new DefaultItemRegistry(trainingMode);
    }
}
