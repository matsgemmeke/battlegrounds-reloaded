package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgeableHitboxProviderTest {

    private static final RelativeHitbox ADULT_STANDING_HITBOX = new RelativeHitbox(Collections.emptySet());
    private static final RelativeHitbox BABY_STANDING_HITBOX = new RelativeHitbox(Collections.emptySet());

    private final AgeableHitboxProvider<Zombie> hitboxProvider = new AgeableHitboxProvider<>(ADULT_STANDING_HITBOX, BABY_STANDING_HITBOX);

    @Test
    void provideHitboxReturnsHitboxForBabyStandingWhenEntityIsNoAdult() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.isAdult()).thenReturn(false);

        Hitbox hitbox = hitboxProvider.provideHitbox(zombie);

        assertThat(hitbox.getComponents()).isSameAs(BABY_STANDING_HITBOX.components());
    }

    @Test
    void provideHitboxReturnsHitboxForAdultStandingWhenEntityIsAdult() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.isAdult()).thenReturn(true);

        Hitbox hitbox = hitboxProvider.provideHitbox(zombie);

        assertThat(hitbox.getComponents()).isSameAs(ADULT_STANDING_HITBOX.components());
    }
}
