package nl.matsgemmeke.battlegrounds.entity.damage;

import nl.matsgemmeke.battlegrounds.entity.EntityKey;

import java.util.UUID;

public interface DamageSource {

    EntityKey getEntityKey();

    UUID getUniqueId();
}
