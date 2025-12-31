package nl.matsgemmeke.battlegrounds.item.trigger.impact;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpactTriggerTest {

    private static final UUID SOURCE_ID = UUID.randomUUID();

    @Mock
    private TriggerTarget target;

    private ImpactTrigger trigger;

    @BeforeEach
    void setUp() {
        trigger = new ImpactTrigger();
    }

    @Test
    void activatesReturnsFalseWhenTargetDoesNotExist() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(false);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenTargetVelocityIsZero() {
        Vector velocity = new Vector();
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(true);
        when(target.getVelocity()).thenReturn(velocity);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenCastRayTraceResultIsNull() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(null);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenCastRayTraceResultHasNoHitBlock() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Block) null, null);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenCastRayTraceResultHasNoHitBlockFace() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        Block hitBlock = mock(Block.class);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, null);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenCastRayTraceResultHitsBlockThatIsNotSolid() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.AIR);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsTrueWhenCastRayTraceResultHitsSolidBlock() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.STONE);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isTrue();
    }
}
