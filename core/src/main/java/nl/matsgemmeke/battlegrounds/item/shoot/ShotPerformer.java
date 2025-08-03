package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.ItemReceiver;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilReceiver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface ShotPerformer extends ItemReceiver, ProjectileLaunchSource, RecoilReceiver {

    Entity getEntity();

    /**
     * Gets the direction of where the entity would shoot.
     *
     * @return the entity's shooting direction
     */
    @NotNull
    Location getShootingDirection();
}
