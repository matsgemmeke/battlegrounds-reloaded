package nl.matsgemmeke.battlegrounds.entity.hitbox.impl;

import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitboxDefaults;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerHitboxTest {

    @Mock
    private Player player;

    private PlayerHitbox playerHitbox;

    @BeforeEach
    void setUp() {
        PositionHitbox standingHitbox = PositionHitboxDefaults.PLAYER_STANDING;

        playerHitbox = new PlayerHitbox(player, standingHitbox);
    }

    @Test
    void getHitboxComponentTypeReturnsEmptyOptionalWhenGivenLocationWorldDoesNotEqualPlayerWorld() {
        Location location = new Location(mock(World.class), 10, 10, 10);
        Location playerLocation = new Location(mock(World.class), 10, 10, 10);

        when(player.getLocation()).thenReturn(playerLocation);

        Optional<HitboxComponentType> hitboxComponentTypeOptional = playerHitbox.getHitboxComponentType(location);

        assertThat(hitboxComponentTypeOptional).isEmpty();
    }

    @Test
    void intersectsReturnsFalseWhenGivenLocationWorldDoesNotEqualPlayerWorld() {
        Location location = new Location(mock(World.class), 10, 10, 10);
        Location playerLocation = new Location(mock(World.class), 10, 10, 10);

        when(player.getLocation()).thenReturn(playerLocation);

        boolean intersects = playerHitbox.intersects(location);

        assertThat(intersects).isFalse();
    }

    @Test
    void intersectsReturnsTrueWhenGivenLocationIsInsidePlayerHitboxWhenStanding() {
        World world = mock(World.class);
        Location location = new Location(world, 10.1, 11.5, 9.9);
        Location playerLocation = new Location(world, 10, 10, 10);

        when(player.getLocation()).thenReturn(playerLocation);
        when(player.isSleeping()).thenReturn(false);
        when(player.isSneaking()).thenReturn(false);

        boolean intersects = playerHitbox.intersects(location);

        assertThat(intersects).isTrue();
    }
}
