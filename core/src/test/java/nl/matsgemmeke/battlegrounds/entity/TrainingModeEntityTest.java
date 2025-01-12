package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

public class TrainingModeEntityTest {

    private LivingEntity entity;

    @BeforeEach
    public void setUp() {
        this.entity = mock(LivingEntity.class);
    }

    @Test
    public void getLastDamageReturnsNullIfEntityHasNotTakenDamage() {
        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        Damage lastDamage = trainingModeEntity.getLastDamage();

        assertNull(lastDamage);
    }

    @Test
    public void getLastDamageReturnsLastDamageDealtToPlayer() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(entity.getHealth()).thenReturn(20.0);
        when(entity.isDead()).thenReturn(false);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        trainingModeEntity.damage(damage);
        Damage lastDamage = trainingModeEntity.getLastDamage();

        assertEquals(damage, lastDamage);
    }

    @NotNull
    private static Stream<Arguments> damageScenarios() {
        return Stream.of(
                arguments(50.0, 50.0, 20.0, 10.0),
                arguments(5000.0, 5000.0, 20.0, 0.0)
        );
    }

    @ParameterizedTest
    @MethodSource("damageScenarios")
    public void damageReturnsDealtDamageAndLowersHealth(
            double damageAmount,
            double expectedDamageDealt,
            double health,
            double expectedHealth
    ) {
        when(entity.getHealth()).thenReturn(health);

        Damage damage = new Damage(damageAmount, DamageType.BULLET_DAMAGE);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        double damageDealt = trainingModeEntity.damage(damage);

        assertEquals(expectedDamageDealt, damageDealt);

        verify(entity).setHealth(expectedHealth);
    }

    @Test
    public void damageDoesNotApplyDamageIfEntityIsDead() {
        when(entity.isDead()).thenReturn(true);

        Damage damage = new Damage(50.0, DamageType.BULLET_DAMAGE);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        double damageDealt = trainingModeEntity.damage(damage);

        assertEquals(0.0, damageDealt);

        verify(entity, never()).setHealth(anyDouble());
    }

    @Test
    public void damageDoesNotApplyDamageIfHealthIsBelowZero() {
        when(entity.getHealth()).thenReturn(0.0);

        Damage damage = new Damage(50.0, DamageType.BULLET_DAMAGE);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        double damageDealt = trainingModeEntity.damage(damage);

        assertEquals(0.0, damageDealt);

        verify(entity, never()).setHealth(anyDouble());
    }
}
