package nl.matsgemmeke.battlegrounds.game.component.projectile;

import org.bukkit.Location;

@FunctionalInterface
public interface ProjectileHitAction {

    void onProjectileHit(Location hitLocation);
}
