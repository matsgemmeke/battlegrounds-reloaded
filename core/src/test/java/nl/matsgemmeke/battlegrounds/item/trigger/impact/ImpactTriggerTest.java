package nl.matsgemmeke.battlegrounds.item.trigger.impact;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ImpactTriggerTest {

    private Entity entity;
    private TriggerContext context;
    private TriggerTarget target;

    @BeforeEach
    public void setUp() {
        entity = mock(Entity.class);
        target = mock(TriggerTarget.class);
        context = new TriggerContext(entity, target);
    }

    @Test
    public void activatesReturnsFalseWhenTargetDoesNotExist() {
        when(target.exists()).thenReturn(false);

        ImpactTrigger trigger = new ImpactTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsFalseWhenTargetVelocityIsZero() {
        Vector velocity = new Vector();

        when(target.exists()).thenReturn(true);
        when(target.getVelocity()).thenReturn(velocity);

        ImpactTrigger trigger = new ImpactTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsFalseWhenCastRayTraceResultIsNull() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(null);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsFalseWhenCastRayTraceResultHasNoHitBlock() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Block) null, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsFalseWhenCastRayTraceResultHasNoHitBlockFace() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);
        Block hitBlock = mock(Block.class);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, null);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsFalseWhenCastRayTraceResultHitsBlockThatIsNotSolid() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.AIR);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsTrueWhenCastRayTraceResultHitsSolidBlock() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, -1, 1);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(Material.STONE);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), hitBlock, BlockFace.NORTH);

        World world = mock(World.class);
        when(world.rayTraceBlocks(targetLocation, velocity, 1.0)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        ImpactTrigger trigger = new ImpactTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isTrue();
    }
}
