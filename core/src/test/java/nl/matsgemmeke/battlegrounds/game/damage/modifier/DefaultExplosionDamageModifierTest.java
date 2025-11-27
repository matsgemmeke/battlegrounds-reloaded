package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultExplosionDamageModifierTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final double DAMAGE = 100.0;

    @Mock
    private Entity damager;
    @Mock
    private Entity entity;
    @InjectMocks
    private DefaultExplosionDamageModifier modifier;

    @Test
    void shouldNotAlterDamageForEventsWithoutDefaultExplosionDamageCause() {
        DamageEvent event = new DamageEvent(damager, GAME_KEY, entity, GAME_KEY, DamageType.BULLET_DAMAGE, DAMAGE);

        modifier.apply(event);

        assertThat(event.getDamage()).isEqualTo(DAMAGE);
    }

    @Test
    void negateExplosionDamageForEventsWithDefaultExplosionDamageCause() {
        DamageEvent event = new DamageEvent(damager, GAME_KEY, entity, GAME_KEY, DamageType.EXPLOSIVE_DAMAGE, DAMAGE);

        modifier.apply(event);

        assertThat(event.getDamage()).isEqualTo(0.0);
    }

    @ParameterizedTest
    @CsvSource({ "BULLET_DAMAGE,100.0", "EXPLOSIVE_DAMAGE,0.0" })
    void applyReturnsDamageEventWithModifiedDamageDependingOnDamageType(DamageType damageType, double expectedDamageAmount) {
        DamageNew damage = new DamageNew(DAMAGE, damageType);
        DamageEventNew damageEvent = new DamageEventNew(entity, damager, damage);

        DamageEventNew result = modifier.apply(damageEvent);

        assertThat(result.damage().amount()).isEqualTo(expectedDamageAmount);
    }
}
