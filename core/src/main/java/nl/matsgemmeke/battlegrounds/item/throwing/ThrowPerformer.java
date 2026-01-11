package nl.matsgemmeke.battlegrounds.item.throwing;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.ItemReceiver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import org.bukkit.Location;

/**
 * An entity that can perform item throws.
 */
public interface ThrowPerformer extends DamageSource, ItemReceiver, ProjectileLaunchSource {

    /**
     * Gets the direction of where the entity would throw towards.
     *
     * @return the entity's shooting direction
     */
    Location getThrowDirection();
}
