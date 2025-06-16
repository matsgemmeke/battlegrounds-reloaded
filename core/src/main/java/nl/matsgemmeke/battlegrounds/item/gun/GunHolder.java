package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilReceiver;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import nl.matsgemmeke.battlegrounds.item.shoot.ShotPerformer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * An entity that is capable of owning and operating a {@link Gun}.
 */
public interface GunHolder extends ItemHolder, RecoilReceiver, ReloadPerformer, ScopeUser, ShotPerformer {

    /**
     * Gets the direction of where the holder would shoot.
     *
     * @return the shooting direction
     */
    @NotNull
    Location getShootingDirection();
}
