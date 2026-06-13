package nl.matsgemmeke.battlegrounds.entity.damage;

import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import org.bukkit.Location;

import java.util.UUID;

public interface DamageTarget {

    EntityKey getEntityKey();

    double getHealth();

    void setHealth(double health);

    Hitbox getHitbox();

    Location getLocation();

    UUID getUniqueId();

    double damage(Damage damage);
}
