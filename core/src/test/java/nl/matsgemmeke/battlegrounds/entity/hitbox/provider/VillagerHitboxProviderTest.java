package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VillagerHitboxProviderTest {

    private static final RelativeHitbox ADULT_STANDING_HITBOX = new RelativeHitbox(new HashSet<>());
    private static final RelativeHitbox ADULT_SLEEPING_HITBOX = new RelativeHitbox(new HashSet<>());
    private static final RelativeHitbox BABY_STANDING_HITBOX = new RelativeHitbox(new HashSet<>());
    private static final RelativeHitbox BABY_SLEEPING_HITBOX = new RelativeHitbox(new HashSet<>());

    private final VillagerHitboxProvider hitboxProvider = new VillagerHitboxProvider(ADULT_STANDING_HITBOX, ADULT_SLEEPING_HITBOX, BABY_STANDING_HITBOX, BABY_SLEEPING_HITBOX);

    @Test
    void provideHitboxThrowsHitboxProvisionExceptionWhenGivenEntityIsNoVillager() {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        assertThatThrownBy(() -> hitboxProvider.provideHitbox(player))
                .isInstanceOf(HitboxProvisionException.class)
                .hasMessage("Entity PLAYER is not compatible: expected a villager entity");
    }

    static List<Arguments> standingScenarios() {
        return List.of(arguments(true, ADULT_STANDING_HITBOX), arguments(false, BABY_STANDING_HITBOX));
    }

    @ParameterizedTest
    @MethodSource("standingScenarios")
    void provideHitboxReturnsStandingHitboxWhenEntityIsStanding(boolean adult, RelativeHitbox expectedRelativeHitbox) {
        Villager villager = mock(Villager.class);
        when(villager.isAdult()).thenReturn(adult);
        when(villager.isSleeping()).thenReturn(false);

        Hitbox hitbox = hitboxProvider.provideHitbox(villager);

        assertThat(hitbox.getComponents()).isSameAs(expectedRelativeHitbox.components());
    }

    static List<Arguments> sleepingScenarios() {
        return List.of(arguments(true, ADULT_SLEEPING_HITBOX), arguments(false, BABY_SLEEPING_HITBOX));
    }

    @ParameterizedTest
    @MethodSource("sleepingScenarios")
    void provideHitboxReturnsSleepingHitboxWhenEntityIsSleeping(boolean adult, RelativeHitbox expectedRelativeHitbox) {
        Location villagerLocation = new Location(null, 1, 1, 1);

        Bed bed = mock(Bed.class);
        when(bed.getFacing()).thenReturn(BlockFace.EAST);

        World world = mock(World.class, Mockito.RETURNS_DEEP_STUBS);
        when(world.getBlockAt(villagerLocation).getBlockData()).thenReturn(bed);

        villagerLocation.setWorld(world);

        Villager villager = mock(Villager.class);
        when(villager.getLocation()).thenReturn(villagerLocation);
        when(villager.isAdult()).thenReturn(adult);
        when(villager.isSleeping()).thenReturn(true);

        Hitbox hitbox = hitboxProvider.provideHitbox(villager);

        assertThat(hitbox.getComponents()).isSameAs(expectedRelativeHitbox.components());
    }

    @Test
    void provideHitboxReturnsStandingHitboxWhenEntityIsSleepingButTheirLocationIsNoBed() {
        Location villagerLocation = new Location(null, 1, 1, 1);

        World world = mock(World.class, Mockito.RETURNS_DEEP_STUBS);
        when(world.getBlockAt(villagerLocation).getBlockData()).thenReturn(mock(Sign.class));

        villagerLocation.setWorld(world);

        Villager villager = mock(Villager.class);
        when(villager.getLocation()).thenReturn(villagerLocation);
        when(villager.isAdult()).thenReturn(true);
        when(villager.isSleeping()).thenReturn(true);

        Hitbox hitbox = hitboxProvider.provideHitbox(villager);

        assertThat(hitbox.getComponents()).isSameAs(ADULT_STANDING_HITBOX.components());
    }
}
