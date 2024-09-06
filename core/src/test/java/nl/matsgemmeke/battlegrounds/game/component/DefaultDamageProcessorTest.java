package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.damage.DamageCause;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.check.DamageCheck;
import org.bukkit.entity.Entity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultDamageProcessorTest {

    @Test
    public void shouldProcessAllDamageCheckForDamageEventAndReturnResult() {
        DamageCheck check = mock(DamageCheck.class);
        Entity damager = mock(Entity.class);
        Entity entity = mock(Entity.class);

        DamageEvent event = new DamageEvent(damager, entity, DamageCause.GUN_PROJECTILE, 10.0);

        DefaultDamageProcessor processor = new DefaultDamageProcessor();
        processor.addDamageCheck(check);

        DamageEvent result = processor.processDamage(event);

        assertEquals(event, result);

        verify(check).process(event);
    }
}
