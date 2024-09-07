package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.entity.Mob;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

public class DefaultGameMobTest {

    private Mob mob;

    @Before
    public void setUp() {
        mob = mock(Mob.class);
    }

    @Test
    public void appliesDamageToEntity() {
        when(mob.getHealth()).thenReturn(20.0);

        DefaultGameMob gameMob = new DefaultGameMob(mob);
        double result = gameMob.damage(50.0);

        assertEquals(10.0, result, 0.0);
    }

    @Test
    public void doesNotDamageEntityWhenDead() {
        when(mob.isDead()).thenReturn(true);

        DefaultGameMob gameMob = new DefaultGameMob(mob);
        gameMob.damage(10.0);

        verify(mob, never()).setHealth(anyDouble());
    }

    @Test
    public void doesNotDamageEntityWhenHealthIsBelowZero() {
        when(mob.getHealth()).thenReturn(0.0);

        DefaultGameMob gameMob = new DefaultGameMob(mob);
        gameMob.damage(10.0);

        verify(mob, never()).setHealth(anyDouble());
    }
}
