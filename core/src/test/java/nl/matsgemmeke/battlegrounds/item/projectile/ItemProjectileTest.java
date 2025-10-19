package nl.matsgemmeke.battlegrounds.item.projectile;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemProjectileTest {

    @Mock
    private Item item;
    @InjectMocks
    private ItemProjectile itemProjectile;

    @Test
    void getLocationReturnsItemLocation() {
        Location itemLocation = new Location(null, 1, 1, 1);

        when(item.getLocation()).thenReturn(itemLocation);

        Location projectileLocation = itemProjectile.getLocation();

        assertThat(projectileLocation).isEqualTo(itemLocation);
    }

    @Test
    void getVelocityReturnsItemVelocity() {
        Vector itemVelocity = new Vector();

        when(item.getVelocity()).thenReturn(itemVelocity);

        Vector projectileVelocity = itemProjectile.getVelocity();

        assertThat(projectileVelocity).isEqualTo(itemVelocity);
    }

    @Test
    void setVelocitySetsItemsVelocity() {
        Vector velocity = new Vector();

        itemProjectile.setVelocity(velocity);

        verify(item).setVelocity(velocity);
    }

    @Test
    void getWorldReturnsItemWorld() {
        World itemWorld = mock(World.class);

        when(item.getWorld()).thenReturn(itemWorld);

        World projectileWorld = itemProjectile.getWorld();

        assertThat(projectileWorld).isEqualTo(itemWorld);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void hasGravityReturnsWhetherItemHasGravity(boolean itemGravity) {
        when(item.hasGravity()).thenReturn(itemGravity);

        boolean projectileGravity = itemProjectile.hasGravity();

        assertThat(projectileGravity).isEqualTo(itemGravity);
    }

    @Test
    void setGravitySetsGravityOfItem() {
        itemProjectile.setGravity(true);

        verify(item).setGravity(true);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void existsReturnsFalseWhenItemIsDeadAndTrueOtherwise(boolean dead) {
        when(item.isDead()).thenReturn(dead);

        boolean exists = itemProjectile.exists();

        assertThat(exists).isEqualTo(!dead);
    }

    @Test
    void removeRemovesItem() {
        itemProjectile.remove();

        verify(item).remove();
    }
}
