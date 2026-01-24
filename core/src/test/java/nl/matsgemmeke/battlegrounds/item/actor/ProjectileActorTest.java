package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectileActorTest {

    @Mock
    private Projectile projectile;
    @InjectMocks
    private ProjectileActor actor;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    void existsReturnsWhetherProjectileIsValid(boolean valid, boolean shouldExist) {
        when(projectile.isValid()).thenReturn(valid);

        boolean exists = actor.exists();

        assertThat(exists).isEqualTo(shouldExist);
    }

    @Test
    void getLocationReturnsProjectileLocation() {
        Location projectileLocation = new Location(null, 1, 1, 1);

        when(projectile.getLocation()).thenReturn(projectileLocation);

        Location triggerTargetLocation = actor.getLocation();

        assertThat(triggerTargetLocation).isEqualTo(projectileLocation);
    }

    @Test
    void getVelocityReturnsProjectileVelocity() {
        Vector projectileVelocity = new Vector(1, 1, 1);

        when(projectile.getVelocity()).thenReturn(projectileVelocity);

        Vector triggerTargetVelocity = actor.getVelocity();

        assertThat(triggerTargetVelocity).isEqualTo(projectileVelocity);
    }

    @Test
    void getWorldReturnsProjectileWorld() {
        World projectileWorld = mock(World.class);

        when(projectile.getWorld()).thenReturn(projectileWorld);

        World triggerTargetWorld = actor.getWorld();

        assertThat(triggerTargetWorld).isEqualTo(projectileWorld);
    }
}
