package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultEntityHitboxProviderTest {

    private final DefaultEntityHitboxProvider hitboxProvider = new DefaultEntityHitboxProvider();

    @Test
    void provideHitboxReturnsHitboxBasedOnBoundingBoxOfGivenEntity() {
        Location baseLocation = new Location(null, 0.5, 0, 0.5);

        Location corner1 = new Location(null, 0, 0, 0);
        Location corner2 = new Location(null, 1, 1, 1);
        BoundingBox boundingBox = BoundingBox.of(corner1, corner2);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(baseLocation);
        when(entity.getBoundingBox()).thenReturn(boundingBox);

        Hitbox hitbox = hitboxProvider.provideHitbox(entity);

        assertThat(hitbox.getComponents()).satisfiesExactly(component -> {
           assertThat(component.type()).isEqualTo(HitboxComponentType.TORSO);
           assertThat(component.width()).isOne();
           assertThat(component.height()).isOne();
           assertThat(component.depth()).isOne();
           assertThat(component.offsetX()).isZero();
           assertThat(component.offsetY()).isZero();
           assertThat(component.offsetZ()).isZero();
        });
    }
}
