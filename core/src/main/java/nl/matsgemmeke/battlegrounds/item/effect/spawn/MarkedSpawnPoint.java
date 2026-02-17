package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class MarkedSpawnPoint implements SpawnPoint {

    private final Actor actor;
    private final float yaw;

    public MarkedSpawnPoint(Actor actor, float yaw) {
        this.actor = actor;
        this.yaw = yaw;
    }

    @NotNull
    public Location getLocation() {
        Location location = actor.getLocation();
        location.setYaw(yaw);
        return location;
    }

    public void onSpawn() {
        if (actor.exists() && actor instanceof Removable removableActor) {
            removableActor.remove();
        }
    }
}
