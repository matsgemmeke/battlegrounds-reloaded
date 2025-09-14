package nl.matsgemmeke.battlegrounds.game.damage;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DamageTypeTest {

    @Test
    public void mapReturnsOptionalWithEntityAttackDamageTypeWhenGivenEntityAttackDamageCause() {
        Optional<DamageType> damageTypeOptional = DamageType.map(DamageCause.ENTITY_ATTACK);

        assertThat(damageTypeOptional).hasValue(DamageType.ATTACK_DAMAGE);
    }

    @Test
    public void mapReturnsOptionalWithNormalExplosionDamageTypeWhenGivenEntityExplosionDamageCause() {
        Optional<DamageType> damageTypeOptional = DamageType.map(DamageCause.ENTITY_EXPLOSION);

        assertThat(damageTypeOptional).hasValue(DamageType.EXPLOSIVE_DAMAGE);
    }

    @Test
    public void mapReturnsEmptyOptionalWhenGivenUnmappedDamageCause() {
        Optional<DamageType> damageTypeOptional = DamageType.map(DamageCause.VOID);

        assertThat(damageTypeOptional).isEmpty();
    }
}
