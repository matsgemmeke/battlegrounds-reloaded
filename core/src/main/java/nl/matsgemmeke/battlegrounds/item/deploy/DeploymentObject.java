package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.Damageable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface DeploymentObject extends Damageable {

    void destroy();

    Location getLocation();

    World getWorld();

    boolean matchesEntity(@NotNull Entity entity);
}
