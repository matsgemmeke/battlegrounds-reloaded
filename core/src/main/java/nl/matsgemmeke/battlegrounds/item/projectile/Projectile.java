package nl.matsgemmeke.battlegrounds.item.projectile;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import org.bukkit.util.Vector;

public interface Projectile extends Actor {

    void setVelocity(Vector velocity);

    boolean hasGravity();

    void setGravity(boolean gravity);
}
