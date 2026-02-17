package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
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
    private Actor actor;

    private final BlockImpactTrigger trigger = new BlockImpactTrigger();

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenActorDoesNotExist() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        when(actor.exists()).thenReturn(false);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenActorVelocityIsZero() {
        Vector velocity = new Vector();
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        when(actor.exists()).thenReturn(true);
        when(actor.getVelocity()).thenReturn(velocity);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenCastRayTraceResultIsNull() {
        Location actorLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        World world = mock(World.class);
        when(world.rayTraceBlocks(actorLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(null);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getVelocity()).thenReturn(velocity);
        when(actor.getWorld()).thenReturn(world);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenCastRayTraceResultHasNoHitBlock() {
        Location actorLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Block) null, null);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        World world = mock(World.class);
        when(world.rayTraceBlocks(actorLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getVelocity()).thenReturn(velocity);
        when(actor.getWorld()).thenReturn(world);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenHitBlockTypeIsNotSolid() {
        Location actorLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.WATER);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(actorLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getVelocity()).thenReturn(velocity);
        when(actor.getWorld()).thenReturn(world);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsBlockTriggerResultWhenCastRayTraceResultHitsSolidBlock() {
        Location actorLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        Vector hitPosition = new Vector(2, 2, 2);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.STONE);

        RayTraceResult rayTraceResult = new RayTraceResult(hitPosition, hitBlock, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(actorLocation, velocity, 3.0, FluidCollisionMode.NEVER, true)).thenReturn(rayTraceResult);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getVelocity()).thenReturn(velocity);
        when(actor.getWorld()).thenReturn(world);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isTrue();
        assertThat(triggerResult).isInstanceOfSatisfying(BlockTriggerResult.class, blockTriggerResult -> {
           assertThat(blockTriggerResult.getHitBlock()).isEqualTo(hitBlock);
           assertThat(blockTriggerResult.getHitLocation()).isEqualTo(new Location(world, 2.0, 2.0, 2.0));
        });
    }
}
