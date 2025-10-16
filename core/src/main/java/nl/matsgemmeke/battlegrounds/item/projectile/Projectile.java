package nl.matsgemmeke.battlegrounds.item.projectile;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.util.Vector;

public interface Projectile extends TriggerTarget {

    Vector getVelocity();

    void setVelocity(Vector velocity);

    boolean hasGravity();

    void setGravity(boolean gravity);
}
