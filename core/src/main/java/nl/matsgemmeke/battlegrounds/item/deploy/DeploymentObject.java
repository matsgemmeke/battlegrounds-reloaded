package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.Damageable;
import org.bukkit.Location;

public interface DeploymentObject extends Damageable {

    Location getLocation();
}
