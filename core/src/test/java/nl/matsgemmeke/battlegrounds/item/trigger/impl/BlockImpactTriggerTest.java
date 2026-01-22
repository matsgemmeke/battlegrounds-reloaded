package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockImpactTriggerTest {

    private static final UUID SOURCE_ID = UUID.randomUUID();

    @Mock
    private TriggerTarget target;

    private final BlockImpactTrigger trigger = new BlockImpactTrigger();

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenTargetDoesNotExist() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(false);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenTargetVelocityIsZero() {
        Vector velocity = new Vector();
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(true);
        when(target.getVelocity()).thenReturn(velocity);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenCastRayTraceResultIsNull() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(null);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenCastRayTraceResultHasNoHitBlock() {
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

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenHitBlockTypeIsNotSolid() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.WATER);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsBlockTriggerResultWhenCastRayTraceResultHitsSolidBlock() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        Vector hitPosition = new Vector(2, 2, 2);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.STONE);

        RayTraceResult rayTraceResult = new RayTraceResult(hitPosition, hitBlock, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isTrue();
        assertThat(triggerResult).isInstanceOfSatisfying(BlockTriggerResult.class, blockTriggerResult -> {
           assertThat(blockTriggerResult.getHitBlock()).isEqualTo(hitBlock);
           assertThat(blockTriggerResult.getHitLocation()).isEqualTo(new Location(world, 2.0, 2.0, 2.0));
        });
    }
}
