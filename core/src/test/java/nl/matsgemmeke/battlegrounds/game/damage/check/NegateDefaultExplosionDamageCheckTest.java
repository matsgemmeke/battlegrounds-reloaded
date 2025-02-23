package nl.matsgemmeke.battlegrounds.game.damage.check;

import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class NegateDefaultExplosionDamageCheckTest {

    @Test
    public void shouldNotAlterDamageForEventsWithoutDefaultExplosionDamageCause() {
        Entity damager = mock(Entity.class);
        Entity entity = mock(Entity.class);
        GameKey gameKey = GameKey.ofTrainingMode();

        double damage = 10.0;

        DamageEvent event = new DamageEvent(damager, gameKey, entity, gameKey, DamageType.BULLET_DAMAGE, damage);

        NegateDefaultExplosionDamageCheck check = new NegateDefaultExplosionDamageCheck();
        check.process(event);

        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void negateExplosionDamageForEventsWithDefaultExplosionDamageCause() {
        Entity damager = mock(Entity.class);
        Entity entity = mock(Entity.class);
        GameKey gameKey = GameKey.ofTrainingMode();

        DamageEvent event = new DamageEvent(damager, gameKey, entity, gameKey, DamageType.EXPLOSIVE_DAMAGE, 100.0);

        NegateDefaultExplosionDamageCheck check = new NegateDefaultExplosionDamageCheck();
        check.process(event);

        assertEquals(0.0, event.getDamage(), 0.0);
    }
}
