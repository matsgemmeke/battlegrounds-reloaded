package nl.matsgemmeke.battlegrounds.item.projectile;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Projectile extends TriggerTarget {

    @NotNull
    Vector getVelocity();

    void setVelocity(@NotNull Vector velocity);

    boolean hasGravity();

    void setGravity(boolean gravity);
}
