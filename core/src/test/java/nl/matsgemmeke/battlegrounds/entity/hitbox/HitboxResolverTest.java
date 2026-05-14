package nl.matsgemmeke.battlegrounds.entity.hitbox;

import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.*;
import org.bukkit.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HitboxResolverTest {

    private HitboxResolver hitboxResolver;

    @BeforeEach
    void setUp() {
        hitboxResolver = new HitboxResolver();
    }

    @Test
    @DisplayName("resolveHitboxProvider returns DefaultEntityHitboxProvider when given entity type has no linked hitbox provider")
    void resolveHitboxProvider_unknownEntityType() {
        Entity entity = mock(Entity.class);
        when(entity.getType()).thenReturn(EntityType.UNKNOWN);

        HitboxProvider<Entity> hitboxProvider = hitboxResolver.resolveHitboxProvider(entity);

        assertThat(hitboxProvider).isInstanceOf(DefaultEntityHitboxProvider.class);
    }

    @Test
    @DisplayName("resolveHitboxProvider returns HitboxProvider corresponding with given entity")
    void resolveHitboxProvider_player() {
        Entity entity = mock(Entity.class);
        when(entity.getType()).thenReturn(EntityType.PLAYER);

        HitboxProvider<Player> hitboxProvider = mock();

        hitboxResolver.addEntityHitboxProvider(EntityType.PLAYER, hitboxProvider);
        HitboxProvider<Entity> result = hitboxResolver.resolveHitboxProvider(entity);

        assertThat(result).isSameAs(hitboxProvider);
    }
}
