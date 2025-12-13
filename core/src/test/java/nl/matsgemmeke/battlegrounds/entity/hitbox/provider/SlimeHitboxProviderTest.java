package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.entity.Slime;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SlimeHitboxProviderTest {

    private static final RelativeHitbox STANDING_HITBOX = new RelativeHitbox(Set.of(
            new HitboxComponent(HitboxComponentType.TORSO, 0.5, 0.5, 0.5, 0.0, 0.0, 0.0)
    ));

    private final SlimeHitboxProvider hitboxProvider = new SlimeHitboxProvider(STANDING_HITBOX);

    @Test
    void provideHitboxReturnsStandingHitboxWhenSlimeSizeEquals1() {
        Slime slime = mock(Slime.class);
        when(slime.getSize()).thenReturn(1);

        Hitbox hitbox = hitboxProvider.provideHitbox(slime);

        assertThat(hitbox.getComponents()).isSameAs(STANDING_HITBOX.components());
    }

    @Test
    void provideHitboxReturnsResizedStandingHitboxWhenSlimeSizeDoesNotEqual1() {
        Slime slime = mock(Slime.class);
        when(slime.getSize()).thenReturn(3);

        Hitbox hitbox = hitboxProvider.provideHitbox(slime);

        assertThat(hitbox.getComponents()).satisfiesExactly(component -> {
            assertThat(component.type()).isEqualTo(HitboxComponentType.TORSO);
            assertThat(component.height()).isEqualTo(1.5);
            assertThat(component.width()).isEqualTo(1.5);
            assertThat(component.depth()).isEqualTo(1.5);
            assertThat(component.offsetX()).isZero();
            assertThat(component.offsetY()).isZero();
            assertThat(component.offsetZ()).isZero();
        });
    }
}
