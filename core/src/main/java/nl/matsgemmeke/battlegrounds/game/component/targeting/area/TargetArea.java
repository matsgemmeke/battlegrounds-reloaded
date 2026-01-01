package nl.matsgemmeke.battlegrounds.game.component.targeting.area;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;

public interface TargetArea {

    boolean contains(DamageTarget target, Location origin);
}
