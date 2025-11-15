package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenModeEntityTest {

    @Mock
    private HitboxProvider hitboxProvider;
    @Mock
    private LivingEntity entity;
    @InjectMocks
    private OpenModeEntity openModeEntity;

    @Test
    void getLastDamageReturnsNullIfEntityHasNotTakenDamage() {
        Damage lastDamage = openModeEntity.getLastDamage();

        assertThat(lastDamage).isNull();
    }

    @Test
    void getLastDamageReturnsLastDamageDealtToPlayer() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(entity.getHealth()).thenReturn(20.0);
        when(entity.isDead()).thenReturn(false);

        openModeEntity.damage(damage);
        Damage lastDamage = openModeEntity.getLastDamage();

        assertThat(lastDamage).isEqualTo(damage);
    }

    @NotNull
    static Stream<Arguments> damageScenarios() {
        return Stream.of(
                arguments(50.0, 50.0, 20.0, 10.0),
                arguments(5000.0, 5000.0, 20.0, 0.0)
        );
    }

    @ParameterizedTest
    @MethodSource("damageScenarios")
    void damageReturnsDealtDamageAndLowersHealth(double damageAmount, double expectedDamageDealt, double health, double expectedHealth) {
        when(entity.getHealth()).thenReturn(health);

        Damage damage = new Damage(damageAmount, DamageType.BULLET_DAMAGE);

        double damageDealt = openModeEntity.damage(damage);

        assertThat(damageDealt).isEqualTo(expectedDamageDealt);

        verify(entity).setHealth(expectedHealth);
    }

    @Test
    void damageDoesNotApplyDamageIfEntityIsDead() {
        when(entity.isDead()).thenReturn(true);

        Damage damage = new Damage(50.0, DamageType.BULLET_DAMAGE);

        OpenModeEntity openModeEntity = new OpenModeEntity(entity, hitboxProvider);
        double damageDealt = openModeEntity.damage(damage);

        assertThat(damageDealt).isZero();

        verify(entity, never()).setHealth(anyDouble());
    }

    @Test
    void damageDoesNotApplyDamageIfHealthIsBelowZero() {
        when(entity.getHealth()).thenReturn(0.0);

        Damage damage = new Damage(50.0, DamageType.BULLET_DAMAGE);

        OpenModeEntity openModeEntity = new OpenModeEntity(entity, hitboxProvider);
        double damageDealt = openModeEntity.damage(damage);

        assertThat(damageDealt).isZero();

        verify(entity, never()).setHealth(anyDouble());
    }

    @Test
    void getHitboxReturnsHitboxInstanceCreatedFromHitboxProvider() {
        PositionHitbox hitbox = mock(PositionHitbox.class);

        when(hitboxProvider.provideHitbox(entity)).thenReturn(hitbox);

        PositionHitbox result = openModeEntity.getHitbox();

        assertThat(result).isEqualTo(hitbox);
    }
}
