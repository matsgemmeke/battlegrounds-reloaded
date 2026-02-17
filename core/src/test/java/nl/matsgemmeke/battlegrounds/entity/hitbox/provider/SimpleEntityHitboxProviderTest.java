package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SimpleEntityHitboxProviderTest {

    private static final RelativeHitbox STANDING_HITBOX = new RelativeHitbox(Collections.emptySet());

    private final SimpleEntityHitboxProvider hitboxProvider = new SimpleEntityHitboxProvider(STANDING_HITBOX);

    @Test
    void provideHitboxReturnsHitboxForStanding() {
        Entity entity = mock(Entity.class);

        Hitbox hitbox = hitboxProvider.provideHitbox(entity);

        assertThat(hitbox.getComponents()).isSameAs(STANDING_HITBOX.components());
    }
}
