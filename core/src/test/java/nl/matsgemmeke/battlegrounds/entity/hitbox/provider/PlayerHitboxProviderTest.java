package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerHitboxProviderTest {

    private static final RelativeHitbox STANDING_HITBOX = new RelativeHitbox(Set.of(new HitboxComponent(null, 1, 1, 1, 1, 1, 1)));
    private static final RelativeHitbox SNEAKING_HITBOX = new RelativeHitbox(Set.of(new HitboxComponent(null, 2, 2, 2, 2, 2, 2)));
    private static final RelativeHitbox SLEEPING_HITBOX = new RelativeHitbox(Set.of(new HitboxComponent(null, 3, 3, 3, 3, 3, 3)));

    private final PlayerHitboxProvider hitboxProvider = new PlayerHitboxProvider(STANDING_HITBOX, SNEAKING_HITBOX, SLEEPING_HITBOX);

    @Test
    void provideHitboxThrowsHitboxProvisionExceptionWhenGivenEntityIsNoPlayer() {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);

        assertThatThrownBy(() -> hitboxProvider.provideHitbox(zombie))
                .isInstanceOf(HitboxProvisionException.class)
                .hasMessage("Cannot provide a hitbox for an entity ZOMBIE as it is not a player");
    }

    @Test
    void provideHitboxReturnsSneakingHitboxWhenPlayerIsSneaking() {
        Player player = mock(Player.class);
        when(player.getPose()).thenReturn(Pose.SNEAKING);

        Hitbox hitbox = hitboxProvider.provideHitbox(player);

        assertThat(hitbox.getComponents()).containsAll(SNEAKING_HITBOX.components());
    }

    @Test
    void provideHitboxReturnsSleepingHitboxWhenPlayerIsSleeping() {
        Location location = new Location(null, 1, 1, 1);

        Bed bed = mock(Bed.class);
        when(bed.getFacing()).thenReturn(BlockFace.EAST);

        World world = mock(World.class, Mockito.RETURNS_DEEP_STUBS);
        when(world.getBlockAt(location).getBlockData()).thenReturn(bed);

        location.setWorld(world);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);
        when(player.getPose()).thenReturn(Pose.SLEEPING);

        Hitbox hitbox = hitboxProvider.provideHitbox(player);

        assertThat(hitbox.getComponents()).containsAll(SLEEPING_HITBOX.components());
    }

    @Test
    void provideHitboxReturnsStandingHitboxWhenPlayerIsSleepingButTheirLocationIsNoBed() {
        Location location = new Location(null, 1, 1, 1);

        World world = mock(World.class, Mockito.RETURNS_DEEP_STUBS);
        when(world.getBlockAt(location).getBlockData()).thenReturn(mock(Sign.class));

        location.setWorld(world);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);
        when(player.getPose()).thenReturn(Pose.SLEEPING);

        Hitbox hitbox = hitboxProvider.provideHitbox(player);

        assertThat(hitbox.getComponents()).containsAll(STANDING_HITBOX.components());
    }

    @Test
    void provideHitboxReturnsStandingHitboxWhenPlayerHasDefaultPose() {
        Player player = mock(Player.class);
        when(player.getPose()).thenReturn(Pose.STANDING);

        Hitbox hitbox = hitboxProvider.provideHitbox(player);

        assertThat(hitbox.getComponents()).containsAll(STANDING_HITBOX.components());
    }
}
