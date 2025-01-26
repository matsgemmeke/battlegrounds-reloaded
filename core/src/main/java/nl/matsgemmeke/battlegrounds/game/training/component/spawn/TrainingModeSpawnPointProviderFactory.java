package nl.matsgemmeke.battlegrounds.game.training.component.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import org.jetbrains.annotations.NotNull;

public interface TrainingModeSpawnPointProviderFactory {

    @NotNull
    SpawnPointProvider make(@NotNull SpawnPointStorage spawnPointStorage);
}
