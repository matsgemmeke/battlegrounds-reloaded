package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.damage.*;
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
    private GameEntity damager;
    @Mock
    private GameEntity entity;
    @InjectMocks
    private DefaultExplosionDamageModifier modifier;

    @ParameterizedTest
    @CsvSource({ "BULLET_DAMAGE,100.0", "EXPLOSIVE_DAMAGE,0.0" })
    void applyReturnsDamageEventWithModifiedDamageDependingOnDamageType(DamageType damageType, double expectedDamageAmount) {
        DamageNew damage = new DamageNew(DAMAGE, damageType);
        EntityDamageEvent entityDamageEvent = new EntityDamageEvent(entity, damager, damage);

        EntityDamageEvent result = modifier.apply(entityDamageEvent);

        assertThat(result.damage().amount()).isEqualTo(expectedDamageAmount);
    }
}
