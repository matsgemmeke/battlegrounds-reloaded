package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.ItemReceiver;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilReceiver;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface ShotPerformer extends ItemReceiver, RecoilReceiver {

    /**
     * Gets the direction of where the entity would shoot.
     *
     * @return the entity's shooting direction
     */
    @NotNull
    Location getShootingDirection();
}
