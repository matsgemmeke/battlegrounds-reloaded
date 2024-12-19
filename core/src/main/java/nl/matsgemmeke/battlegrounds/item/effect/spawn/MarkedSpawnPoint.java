package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class MarkedSpawnPoint implements SpawnPoint {

    @NotNull
    private EffectSource source;

    public MarkedSpawnPoint(@NotNull EffectSource source) {
        this.source = source;
    }

    @NotNull
    public Location getLocation() {
        return source.getLocation();
    }

    public void onSpawn(@NotNull GameEntity gameEntity) {
        if (source.exists()) {
            source.remove();
        }
    }
}
