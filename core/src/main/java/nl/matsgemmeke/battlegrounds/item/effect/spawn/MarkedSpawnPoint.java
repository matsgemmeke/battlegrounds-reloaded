package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MarkedSpawnPoint implements SpawnPoint {

    private float yaw;
    @NotNull
    private ItemEffectSource source;

    public MarkedSpawnPoint(@NotNull ItemEffectSource source, float yaw) {
        this.source = source;
        this.yaw = yaw;
    }

    @NotNull
    public Location getLocation() {
        Location location = source.getLocation();
        location.setYaw(yaw);
        return location;
    }

    public void onSpawn(@NotNull Entity entity) {
        if (source.exists()) {
            source.remove();
        }
    }
}
