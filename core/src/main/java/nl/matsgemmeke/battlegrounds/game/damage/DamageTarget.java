package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

public interface DamageTarget {

    double damage(Damage damage);

    EntityKey getEntityKey();

    double getHealth();

    void setHealth(double health);

    Hitbox getHitbox();

    Optional<Damage> getLastDamage();

    Location getLocation();

    UUID getUniqueId();
}
