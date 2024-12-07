package nl.matsgemmeke.battlegrounds.item.projectile;

import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ProjectilePropertiesTest {

    @Test
    public void getEffectsReturnsProjectileEffects() {
        ProjectileEffect effect = mock(ProjectileEffect.class);

        ProjectileProperties projectileProperties = new ProjectileProperties();
        projectileProperties.getEffects().add(effect);

        assertEquals(1, projectileProperties.getEffects().size());
        assertEquals(effect, projectileProperties.getEffects().get(0));
    }
}
