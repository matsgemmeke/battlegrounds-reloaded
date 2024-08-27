package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Deployable {

    @Nullable
    Entity getDamageSource();

    @NotNull
    Location getLocation();

    @NotNull
    World getWorld();

    void remove();
}
