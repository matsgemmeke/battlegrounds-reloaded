package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
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
class ZombieHitboxProviderTest {

    private static final PositionHitbox ADULT_STANDING_HITBOX = new PositionHitbox(Collections.emptySet());
    private static final PositionHitbox BABY_STANDING_HITBOX = new PositionHitbox(Collections.emptySet());

    private final ZombieHitboxProvider hitboxProvider = new ZombieHitboxProvider(ADULT_STANDING_HITBOX, BABY_STANDING_HITBOX);

    @Test
    void provideHitboxThrowsHitboxProvisionExceptionWhenGivenEntityIsNoZombie() {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        assertThatThrownBy(() -> hitboxProvider.provideHitbox(player))
                .isInstanceOf(HitboxProvisionException.class)
                .hasMessage("Cannot provide a hitbox for an entity PLAYER as it is not a zombie");
    }

    @Test
    void provideHitboxReturnsHitboxForBabyStandingWhenZombieIsNoAdult() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.isAdult()).thenReturn(false);

        PositionHitbox hitbox = hitboxProvider.provideHitbox(zombie);

        assertThat(hitbox).isSameAs(BABY_STANDING_HITBOX);
    }

    @Test
    void provideHitboxReturnsHitboxForAdultStandingWhenZombieIsAdult() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.isAdult()).thenReturn(true);

        PositionHitbox hitbox = hitboxProvider.provideHitbox(zombie);

        assertThat(hitbox).isSameAs(ADULT_STANDING_HITBOX);
    }
}
