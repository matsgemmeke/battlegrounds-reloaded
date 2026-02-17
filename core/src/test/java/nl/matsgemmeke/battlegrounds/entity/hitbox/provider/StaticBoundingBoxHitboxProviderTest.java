package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticBoundingBoxHitboxProviderTest {

    private static final Location BASE_LOCATION = new Location(null, 1, 1, 1);
    private static final double BOUNDING_BOX_SIZE = 0.5;

    @Test
    void provideHitboxCreatesHitboxFromGivenStaticBoundingBox() {
        StaticBoundingBox boundingBox = new StaticBoundingBox(BASE_LOCATION, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);

        StaticBoundingBoxHitboxProvider hitboxProvider = new StaticBoundingBoxHitboxProvider();
        Hitbox hitbox = hitboxProvider.provideHitbox(boundingBox);

        assertThat(hitbox.getBaseLocation()).isEqualTo(BASE_LOCATION);
        assertThat(hitbox.getComponents()).satisfiesExactly(component -> {
            assertThat(component.type()).isEqualTo(HitboxComponentType.TORSO);
            assertThat(component.width()).isEqualTo(BOUNDING_BOX_SIZE);
            assertThat(component.height()).isEqualTo(BOUNDING_BOX_SIZE);
            assertThat(component.depth()).isEqualTo(BOUNDING_BOX_SIZE);
            assertThat(component.offsetX()).isZero();
            assertThat(component.offsetY()).isZero();
            assertThat(component.offsetZ()).isZero();
        });
    }
}
