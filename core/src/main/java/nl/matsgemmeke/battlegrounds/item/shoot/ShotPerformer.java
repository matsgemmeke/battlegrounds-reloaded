package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.ItemReceiver;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface ShotPerformer extends ItemReceiver {

    /**
     * Gets the direction of where the entity would shoot.
     *
     * @return the entity's shooting direction
     */
    @NotNull
    Location getShootingDirection();
}
