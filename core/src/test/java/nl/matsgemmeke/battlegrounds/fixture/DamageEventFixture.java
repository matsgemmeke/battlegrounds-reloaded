package nl.matsgemmeke.battlegrounds.fixture;

import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import org.bukkit.entity.EntityType;

import java.time.Instant;
import java.util.UUID;

public final class DamageEventFixture {

    private static final GameKey GAME_KEY = GameKey.ofFreeplay();
    private static final UUID DAMAGER_ID = UUID.fromString("2c11afe2-48f0-4399-9a04-195bb8ac640e");
    private static final EntityKey DAMAGER_ENTITY_KEY = EntityKey.fromEntityType(EntityType.PLAYER);
    private static final UUID VICTIM_ID = UUID.fromString("606c4672-cf52-4913-85e5-984225ceaed1");
    private static final EntityKey VICTIM_ENTITY_KEY = EntityKey.fromEntityType(EntityType.ZOMBIE);
    private static final String ITEM = "MP5";
    private static final double DAMAGE_AMOUNT = 20.0;
    private static final DamageType DAMAGE_TYPE = DamageType.BULLET_DAMAGE;
    private static final HitboxComponentType HITBOX_COMPONENT_TYPE = HitboxComponentType.TORSO;
    private static final double DISTANCE = 30.0;
    private static final boolean KILL = true;
    private static final boolean FRIENDLY_FIRE = false;
    private static final Instant TIMESTAMP = Instant.parse("2026-05-25T18:00:00.00Z");

    public static DamageEvent createDefault() {
        return new DamageEvent(GAME_KEY, DAMAGER_ID, DAMAGER_ENTITY_KEY, VICTIM_ID, VICTIM_ENTITY_KEY, ITEM, DAMAGE_AMOUNT, DAMAGE_TYPE, HITBOX_COMPONENT_TYPE, DISTANCE, KILL, FRIENDLY_FIRE, TIMESTAMP
        );
    }
}
