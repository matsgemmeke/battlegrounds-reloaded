package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface ItemEffectSource extends TriggerTarget {

    boolean exists();

    @NotNull
    Location getLocation();

    @NotNull
    World getWorld();

    boolean isDeployed();

    void remove();
}
