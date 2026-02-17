package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.ItemReceiver;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilReceiver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import org.bukkit.Location;

public interface ShotPerformer extends DamageSource, ItemReceiver, ProjectileLaunchSource, RecoilReceiver {

    /**
     * Gets the direction of where the entity would shoot.
     *
     * @return the entity's shooting direction
     */
    Location getShootingDirection();
}
