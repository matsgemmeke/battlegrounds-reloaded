package nl.matsgemmeke.battlegrounds.entity.hitbox;

import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.HumanoidHitbox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumanoidHitboxTest {

    @Test
    void intersectsReturnsFalseWhenGivenLocationWorldIsNotEqualToEntityWorld() {
        Location entityLocation = new Location(mock(World.class), 10.0, 10.0, 10.0);
        Location location = new Location(mock(World.class), 10.0, 11.0, 10.0);

        LivingEntity entity = mock(Player.class);
        when(entity.getLocation()).thenReturn(entityLocation);

        HumanoidHitbox humanoidHitbox = new HumanoidHitbox(entity);
        boolean intersects = humanoidHitbox.intersects(location);

        assertThat(intersects).isFalse();
    }

    @Test
    void intersectsReturnsFalseWhenGivenLocationIsNotLocatedInsideHitbox() {
        World world = mock(World.class);
        Location entityLocation = new Location(world, 10.0, 10.0, 10.0);
        Location location = new Location(world, 11.0, 11.0, 11.0);

        LivingEntity entity = mock(Player.class);
        when(entity.getLocation()).thenReturn(entityLocation);

        HumanoidHitbox humanoidHitbox = new HumanoidHitbox(entity);
        boolean intersects = humanoidHitbox.intersects(location);

        assertThat(intersects).isFalse();
    }

    @ParameterizedTest
    @CsvSource({ "11.5,true", "10.5,false" })
    void intersectsReturnsTrueWhenGivenLocationIsLocatedInsideHitbox(double locationY, boolean adult) {
        World world = mock(World.class);
        Location entityLocation = new Location(world, 10.0, 10.0, 10.0);
        Location location = new Location(world, 10.0, locationY, 10.0);

        Zombie zombie = mock(Zombie.class);
        when(zombie.getLocation()).thenReturn(entityLocation);
        when(zombie.isAdult()).thenReturn(adult);

        HumanoidHitbox humanoidHitbox = new HumanoidHitbox(zombie);
        boolean intersects = humanoidHitbox.intersects(location);

        assertThat(intersects).isTrue();
    }
}
