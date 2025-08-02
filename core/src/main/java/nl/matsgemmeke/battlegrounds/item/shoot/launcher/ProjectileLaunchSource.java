package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public interface ProjectileLaunchSource {

    <T extends Projectile> T launchProjectile(Class<? extends T> projectileClass, Vector velocity);
}
