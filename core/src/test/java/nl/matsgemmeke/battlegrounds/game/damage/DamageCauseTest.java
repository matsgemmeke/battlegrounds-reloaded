package nl.matsgemmeke.battlegrounds.game.damage;

import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DamageCauseTest {

    @Test
    public void shouldMapEntityAttackToEntityAttack() {
        DamageCause cause = DamageCause.map(EntityDamageEvent.DamageCause.ENTITY_ATTACK);

        assertEquals(DamageCause.ENTITY_ATTACK, cause);
    }

    @Test
    public void shouldMapEntityExplosionToNormalExplosion() {
        DamageCause cause = DamageCause.map(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);

        assertEquals(DamageCause.DEFAULT_EXPLOSION, cause);
    }

    @Test
    public void shouldReturnNullForDamageCausesThatCannotBeMapped() {
        DamageCause cause = DamageCause.map(EntityDamageEvent.DamageCause.VOID);

        assertNull(cause);
    }
}
