package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.RemovableItemEffectSource;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class MarkedSpawnPoint implements SpawnPoint {

    private final float yaw;
    private final ItemEffectSource source;

    public MarkedSpawnPoint(ItemEffectSource source, float yaw) {
        this.source = source;
        this.yaw = yaw;
    }

    @NotNull
    public Location getLocation() {
        Location location = source.getLocation();
        location.setYaw(yaw);
        return location;
    }

    public void onSpawn() {
        if (source.exists() && source instanceof RemovableItemEffectSource removableSource) {
            removableSource.remove();
        }
    }
}
