package nl.matsgemmeke.battlegrounds.game.damage;

import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DamageTypeTest {

    @Test
    public void shouldMapEntityAttackToEntityAttack() {
        DamageType cause = DamageType.map(EntityDamageEvent.DamageCause.ENTITY_ATTACK);

        assertEquals(DamageType.ATTACK_DAMAGE, cause);
    }

    @Test
    public void shouldMapEntityExplosionToNormalExplosion() {
        DamageType cause = DamageType.map(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);

        assertEquals(DamageType.EXPLOSIVE_DAMAGE, cause);
    }

    @Test
    public void shouldReturnNullForDamageCausesThatCannotBeMapped() {
        DamageType cause = DamageType.map(EntityDamageEvent.DamageCause.VOID);

        assertNull(cause);
    }
}
