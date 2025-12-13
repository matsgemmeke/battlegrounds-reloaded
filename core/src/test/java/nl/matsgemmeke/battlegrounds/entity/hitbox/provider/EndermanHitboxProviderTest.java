package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EndermanHitboxProviderTest {

    private static final RelativeHitbox STANDING_HITBOX = new RelativeHitbox(new HashSet<>());
    private static final RelativeHitbox CARRYING_HITBOX = new RelativeHitbox(new HashSet<>());

    private final EndermanHitboxProvider hitboxProvider = new EndermanHitboxProvider(STANDING_HITBOX, CARRYING_HITBOX);

    @Test
    void provideHitboxReturnsCarryingHitboxWhenEndermanIsCarryingSomething() {
        BlockData blockData = mock(BlockData.class);

        Enderman enderman = mock(Enderman.class);
        when(enderman.getCarriedBlock()).thenReturn(blockData);

        Hitbox hitbox = hitboxProvider.provideHitbox(enderman);

        assertThat(hitbox.getComponents()).isSameAs(CARRYING_HITBOX.components());
    }

    @Test
    void provideHitboxReturnsStandingHitboxWhenEndermanIsNotCarryingSomething() {
        Enderman enderman = mock(Enderman.class);
        when(enderman.getCarriedBlock()).thenReturn(null);

        Hitbox hitbox = hitboxProvider.provideHitbox(enderman);

        assertThat(hitbox.getComponents()).isSameAs(STANDING_HITBOX.components());
    }
}
