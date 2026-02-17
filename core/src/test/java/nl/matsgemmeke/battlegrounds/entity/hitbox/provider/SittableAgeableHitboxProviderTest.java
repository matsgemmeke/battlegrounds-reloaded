package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.entity.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SittableAgeableHitboxProviderTest {

    private static final RelativeHitbox ADULT_STANDING_HITBOX = new RelativeHitbox(new HashSet<>());
    private static final RelativeHitbox ADULT_SITTING_HITBOX = new RelativeHitbox(new HashSet<>());
    private static final RelativeHitbox BABY_STANDING_HITBOX = new RelativeHitbox(new HashSet<>());
    private static final RelativeHitbox BABY_SITTING_HITBOX = new RelativeHitbox(new HashSet<>());

    private final SittableAgeableHitboxProvider<Wolf> hitboxProvider = new SittableAgeableHitboxProvider<>(ADULT_STANDING_HITBOX, ADULT_SITTING_HITBOX, BABY_STANDING_HITBOX, BABY_SITTING_HITBOX);

    static List<Arguments> validScenarios() {
        return List.of(
                arguments(true, true, ADULT_SITTING_HITBOX),
                arguments(true, false, ADULT_STANDING_HITBOX),
                arguments(false, true, BABY_SITTING_HITBOX),
                arguments(false, false, BABY_STANDING_HITBOX)
        );
    }

    @ParameterizedTest
    @MethodSource("validScenarios")
    void provideHitboxReturnsAdultSittingHitboxWhenEntityIsAdultAndSitting(boolean adult, boolean sitting, RelativeHitbox expectedRelativeHitbox) {
        Wolf wolf = mock(Wolf.class);
        when(wolf.isAdult()).thenReturn(adult);
        when(wolf.isSitting()).thenReturn(sitting);

        Hitbox hitbox = hitboxProvider.provideHitbox(wolf);

        assertThat(hitbox.getComponents()).isSameAs(expectedRelativeHitbox.components());
    }
}
