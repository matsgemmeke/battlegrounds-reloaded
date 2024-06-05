package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.entity.LivingEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TrainingModeEntityTest {

    private LivingEntity entity;

    @Before
    public void setUp() {
        this.entity = mock(LivingEntity.class);
    }

    @Test
    public void appliesDamageToEntity() {
        when(entity.getHealth()).thenReturn(20.0);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        double result = trainingModeEntity.damage(50.0);

        assertEquals(10.0, result, 0.0);
    }

    @Test
    public void doesNotDamageEntityWhenDead() {
        when(entity.isDead()).thenReturn(true);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        trainingModeEntity.damage(10.0);

        verify(entity, never()).setHealth(anyDouble());
    }

    @Test
    public void doesNotDamageEntityWhenHealthIsBelowZero() {
        when(entity.getHealth()).thenReturn(0.0);

        TrainingModeEntity trainingModeEntity = new TrainingModeEntity(entity);
        trainingModeEntity.damage(10.0);

        verify(entity, never()).setHealth(anyDouble());
    }
}
