package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * An entity that is capable of owning and operating a {@link Gun}.
 */
public interface GunHolder extends ItemHolder, RecoilReceiver, ReloadPerformer, ScopeUser {

    /**
     * Gets the direction of where the holder would shoot.
     *
     * @return the shooting direction
     */
    @NotNull
    Location getShootingDirection();
}
