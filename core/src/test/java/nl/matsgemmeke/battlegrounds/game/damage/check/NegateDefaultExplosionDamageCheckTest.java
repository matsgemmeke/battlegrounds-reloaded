package nl.matsgemmeke.battlegrounds.game.damage.check;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageCause;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import org.bukkit.entity.Entity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class NegateDefaultExplosionDamageCheckTest {

    @Test
    public void shouldNotAlterDamageForEventsWithoutDefaultExplosionDamageCause() {
        Entity damager = mock(Entity.class);
        GameContext damagerContext = mock(GameContext.class);
        Entity entity = mock(Entity.class);
        GameContext entityContext = mock(GameContext.class);

        double damage = 10.0;

        DamageEvent event = new DamageEvent(damager, damagerContext, entity, entityContext, DamageCause.GUN_PROJECTILE, damage);

        NegateDefaultExplosionDamageCheck check = new NegateDefaultExplosionDamageCheck();
        check.process(event);

        assertEquals(damage, event.getDamage(), 0.0);
    }

    @Test
    public void negateExplosionDamageForEventsWithDefaultExplosionDamageCause() {
        Entity damager = mock(Entity.class);
        GameContext damagerContext = mock(GameContext.class);
        Entity entity = mock(Entity.class);
        GameContext entityContext = mock(GameContext.class);

        DamageEvent event = new DamageEvent(damager, damagerContext, entity, entityContext, DamageCause.DEFAULT_EXPLOSION, 100.0);

        NegateDefaultExplosionDamageCheck check = new NegateDefaultExplosionDamageCheck();
        check.process(event);

        assertEquals(0.0, event.getDamage(), 0.0);
    }
}
