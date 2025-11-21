package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DefaultHitboxProviderTest {

    private static final RelativeHitbox STANDING_HITBOX = new RelativeHitbox(Collections.emptySet());

    private final DefaultHitboxProvider hitboxProvider = new DefaultHitboxProvider(STANDING_HITBOX);

    @Test
    void provideHitboxReturnsHitboxForStanding() {
        Zombie
        Entity entity = mock(Entity.class);

        Hitbox hitbox = hitboxProvider.provideHitbox(entity);

        assertThat(hitbox.getComponents()).isSameAs(STANDING_HITBOX.components());
    }
}
