package nl.matsgemmeke.battlegrounds.game.component.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SpawnPointProvider {

    boolean hasSpawnPoint(@NotNull GameEntity gameEntity);

    @NotNull
    Location respawnEntity(@NotNull GameEntity gameEntity);

    void setCustomSpawnPoint(@NotNull GameEntity gameEntity, @Nullable SpawnPoint spawnPoint);
}
