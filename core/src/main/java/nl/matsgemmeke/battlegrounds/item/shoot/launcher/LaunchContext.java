package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public record LaunchContext(Entity entity, ProjectileLaunchSource source, Location direction) {
}
