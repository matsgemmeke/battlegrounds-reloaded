package nl.matsgemmeke.battlegrounds.game.damage;

import java.util.UUID;

public interface DamageSource {

    DamageSourceType getDamageSourceType();

    UUID getUniqueId();
}
