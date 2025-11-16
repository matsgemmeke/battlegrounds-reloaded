package nl.matsgemmeke.battlegrounds.entity.hitbox.mapper;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HitboxMapperTest {

    private final HitboxMapper hitboxMapper = new HitboxMapper();

    @Test
    void mapReturnsPositionHitboxWithValuesFromHitboxDefinition() {
        HitboxComponentDefinition hitboxComponentDefinition = new HitboxComponentDefinition();
        hitboxComponentDefinition.type = "TORSO";
        hitboxComponentDefinition.size = new Double[] { 0.7, 0.4, 0.2 };
        hitboxComponentDefinition.offset = new Double[] { 0.0, 0.7, 0.0 };

        HitboxDefinition hitboxDefinition = new HitboxDefinition();
        hitboxDefinition.components = List.of(hitboxComponentDefinition);

        RelativeHitbox relativeHitbox = hitboxMapper.map(hitboxDefinition);

        assertThat(relativeHitbox.components()).satisfiesExactly(hitboxComponent -> {
            assertThat(hitboxComponent.type()).isEqualTo(HitboxComponentType.TORSO);
            assertThat(hitboxComponent.height()).isEqualTo(0.7);
            assertThat(hitboxComponent.width()).isEqualTo(0.4);
            assertThat(hitboxComponent.depth()).isEqualTo(0.2);
            assertThat(hitboxComponent.offsetX()).isEqualTo(0.0);
            assertThat(hitboxComponent.offsetY()).isEqualTo(0.7);
            assertThat(hitboxComponent.offsetZ()).isEqualTo(0.0);
        });
    }
}
