package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectileActorTest {

    @Mock
    private Projectile projectile;
    @InjectMocks
    private ProjectileActor actor;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("exists returns whether projectile is valid")
    void exists_returnsItemValidState(boolean valid, boolean shouldExist) {
        when(projectile.isValid()).thenReturn(valid);

        boolean exists = actor.exists();

        assertThat(exists).isEqualTo(shouldExist);
    }

    @Test
    @DisplayName("getLocation returns location of projectile")
    void getLocation_returnsProjectileLocation() {
        Location projectileLocation = new Location(null, 1, 1, 1);

        when(projectile.getLocation()).thenReturn(projectileLocation);

        Location triggerTargetLocation = actor.getLocation();

        assertThat(triggerTargetLocation).isEqualTo(projectileLocation);
    }

    @Test
    @DisplayName("getVelocity returns velocity of projectile")
    void getVelocity_returnsProjectileVelocity() {
        Vector projectileVelocity = new Vector(1, 1, 1);

        when(projectile.getVelocity()).thenReturn(projectileVelocity);

        Vector triggerTargetVelocity = actor.getVelocity();

        assertThat(triggerTargetVelocity).isEqualTo(projectileVelocity);
    }

    @Test
    @DisplayName("getWorld returns world of projectile")
    void getWorld_returnsProjectileWorld() {
        World projectileWorld = mock(World.class);

        when(projectile.getWorld()).thenReturn(projectileWorld);

        World triggerTargetWorld = actor.getWorld();

        assertThat(triggerTargetWorld).isEqualTo(projectileWorld);
    }

    @Test
    @DisplayName("remove removes projectile")
    void remove_removesProjectile() {
        actor.remove();

        verify(projectile).remove();
    }
}
