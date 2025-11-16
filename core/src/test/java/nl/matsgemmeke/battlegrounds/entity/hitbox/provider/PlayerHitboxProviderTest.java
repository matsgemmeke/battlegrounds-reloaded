package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
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
class PlayerHitboxProviderTest {

    private static final RelativeHitbox STANDING_HITBOX = new RelativeHitbox(Collections.emptySet());

    private final PlayerHitboxProvider hitboxProvider = new PlayerHitboxProvider(STANDING_HITBOX);

    @Test
    void provideHitboxThrowsHitboxProvisionExceptionWhenGivenEntityIsNoPlayer() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);

        assertThatThrownBy(() -> hitboxProvider.provideHitbox(zombie))
                .isInstanceOf(HitboxProvisionException.class)
                .hasMessage("Cannot provide a hitbox for an entity ZOMBIE as it is not a player");
    }

    @Test
    void provideHitboxReturnsHitboxForStanding() {
        Player player = mock(Player.class);

        PositionHitbox hitbox = hitboxProvider.provideHitbox(player);

        assertThat(hitbox.getComponents()).isSameAs(STANDING_HITBOX.components());
    }
}
