package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TrainingModeEntityTest {

    private LivingEntity entity;

    @BeforeEach
    public void setUp() {
        this.entity = mock(LivingEntity.class);
    }

    @Test
    public void damageReturnsDamageDealtAndAppliesHeartConvertedDamageToEntity() {
        when(entity.getHealth()).thenReturn(20.0);

        double damageAmount = 50.0;
        Damage damage = new Damage(damageAmount, DamageType.BULLET_DAMAGE);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        double damageDealt = trainingModeEntity.damage(damage);

        assertEquals(damageAmount, damageDealt);

        verify(entity).setHealth(10.0);
    }

    @Test
    public void damageReturnsDamageDealtAndSetsEntityHealthToZeroIfDamageIsGreaterThanHealth() {
        when(entity.getHealth()).thenReturn(20.0);

        double damageAmount = 5000.0;
        Damage damage = new Damage(damageAmount, DamageType.BULLET_DAMAGE);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        double damageDealt = trainingModeEntity.damage(damage);

        assertEquals(damageAmount, damageDealt);

        verify(entity).setHealth(0.0);
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
