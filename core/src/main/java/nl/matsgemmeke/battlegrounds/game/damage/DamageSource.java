package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.entity.EntityKey;

import java.util.UUID;

public interface DamageSource {

    EntityKey getEntityKey();

    UUID getUniqueId();
}
