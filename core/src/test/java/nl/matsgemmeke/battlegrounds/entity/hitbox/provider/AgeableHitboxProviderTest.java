package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgeableHitboxProviderTest {

    private static final RelativeHitbox ADULT_STANDING_HITBOX = new RelativeHitbox(Collections.emptySet());
    private static final RelativeHitbox BABY_STANDING_HITBOX = new RelativeHitbox(Collections.emptySet());

    private final AgeableHitboxProvider hitboxProvider = new AgeableHitboxProvider(ADULT_STANDING_HITBOX, BABY_STANDING_HITBOX);

    @Test
    void provideHitboxThrowsHitboxProvisionExceptionWhenGivenEntityIsNotAgeable() {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        assertThatThrownBy(() -> hitboxProvider.provideHitbox(player))
                .isInstanceOf(HitboxProvisionException.class)
                .hasMessage("Cannot provide a hitbox for an entity PLAYER as it is not an ageable entity");
    }

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
