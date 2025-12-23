package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import org.bukkit.Location;

import java.util.UUID;

public interface DamageTarget {

    double damage(Damage damage);

    Hitbox getHitbox();

    Location getLocation();

    UUID getUniqueId();
}
