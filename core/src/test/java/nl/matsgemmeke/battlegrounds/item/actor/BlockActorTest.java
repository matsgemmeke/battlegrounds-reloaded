package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockActorTest {

    private static final Material MATERIAL = Material.STONE;
    private static final Location BLOCK_LOCATION = new Location(null, 1, 1, 1);

    @Mock
    private Block block;

    private BlockActor actor;

    @BeforeEach
    void setUp() {
        actor = new BlockActor(block, MATERIAL);
    }

    @ParameterizedTest
    @CsvSource({ "STONE,true", "DIRT,false" })
    @DisplayName("exists returns whether block still has original material as type")
    void exists_blockHasChanged(Material blockType, boolean shouldExist) {
        when(block.getType()).thenReturn(blockType);

        boolean exists = actor.exists();

        assertThat(exists).isEqualTo(shouldExist);
    }

    @Test
    @DisplayName("getLocation returns center location of block")
    void getLocation_returnsBlockCenter() {
        when(block.getLocation()).thenReturn(BLOCK_LOCATION);

        Location location = actor.getLocation();

        assertThat(location.getX()).isEqualTo(1.5);
        assertThat(location.getY()).isEqualTo(1.5);
        assertThat(location.getZ()).isEqualTo(1.5);
    }

    @Test
    @DisplayName("getVelocity returns zero vector")
    void getVelocity_returnsZeroVector() {
        Vector velocity = actor.getVelocity();

        assertThat(velocity.isZero()).isTrue();
    }

    @Test
    @DisplayName("getWorld returns block's world")
    void getWorld_returnsBlockWorld() {
        World world = mock(World.class);

        when(block.getWorld()).thenReturn(world);

        World result = actor.getWorld();

        assertThat(result).isEqualTo(world);
    }

    @Test
    @DisplayName("remove resets block to air")
    void remove_resetsBlock() {
        actor.remove();

        verify(block).setType(Material.AIR);
    }
}
