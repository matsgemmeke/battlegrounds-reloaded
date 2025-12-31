package nl.matsgemmeke.battlegrounds.item.trigger.tracking;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockTriggerTargetTest {

    private static final Material MATERIAL = Material.STONE;
    private static final Location BLOCK_LOCATION = new Location(null, 1, 1, 1);

    @Mock
    private Block block;

    private BlockTriggerTarget triggerTarget;

    @BeforeEach
    void setUp() {
        triggerTarget = new BlockTriggerTarget(block, MATERIAL);
    }

    @ParameterizedTest
    @CsvSource({ "STONE,true", "DIRT,false" })
    void existsReturnsWhetherBlockStillHasOriginalMaterialAsType(Material blockType, boolean shouldExist) {
        when(block.getType()).thenReturn(blockType);

        boolean exists = triggerTarget.exists();

        assertThat(exists).isEqualTo(shouldExist);
    }

    @Test
    void getLocationReturnsCenterOfBlock() {
        when(block.getLocation()).thenReturn(BLOCK_LOCATION);

        Location location = triggerTarget.getLocation();

        assertThat(location.getX()).isEqualTo(1.5);
        assertThat(location.getY()).isEqualTo(1.5);
        assertThat(location.getZ()).isEqualTo(1.5);
    }

    @Test
    void getVelocityReturnsZeroVector() {
        Vector velocity = triggerTarget.getVelocity();

        assertThat(velocity.isZero()).isTrue();
    }

    @Test
    void getWorldReturnsBlockWorld() {
        World world = mock(World.class);

        when(block.getWorld()).thenReturn(world);

        World result = triggerTarget.getWorld();

        assertThat(result).isEqualTo(world);
    }
}
