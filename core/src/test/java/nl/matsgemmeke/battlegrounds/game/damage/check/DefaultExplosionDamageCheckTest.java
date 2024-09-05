package nl.matsgemmeke.battlegrounds.game.damage.check;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.damage.DamageCause;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DefaultExplosionDamageCheckTest {

    private double damage;
    private GameEntity damager;
    private GameEntity entity;

    @Before
    public void setUp() {
        damage = 10.0;
        damager = mock(GameEntity.class);
        entity = mock(GameEntity.class);
    }

    @Test
    public void shouldNotAlterDamageForEventsWithoutDefaultExplosionDamageCause() {
        DamageEvent event = new DamageEvent(damager, entity, DamageCause.GUN_PROJECTILE, damage);

        DefaultExplosionDamageCheck check = new DefaultExplosionDamageCheck();
        check.process(event);

        assertEquals(10.0, event.getDamage(), 0.0);
    }
}
