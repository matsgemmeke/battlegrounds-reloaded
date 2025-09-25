package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FloorHitTriggerTest {

    private TriggerContext context;
    private TriggerTarget target;

    @BeforeEach
    public void setUp() {
        target = mock(TriggerTarget.class);
        context = new TriggerContext(mock(Entity.class), target);
    }

    @Test
    public void activatesReturnsFalseWhenTargetDoesNotExists() {
        when(target.exists()).thenReturn(false);

        FloorHitTrigger trigger = new FloorHitTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsFalseWhenBlockBelowTargetIsPassable() {
        World world = mock(World.class);
        Location targetLocation = new Location(world, 1, 1, 1);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(true);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);

        FloorHitTrigger trigger = new FloorHitTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    public void activatesReturnsTrueWhenBlockBelowTargetIsNotPassable() {
        World world = mock(World.class);
        Location targetLocation = new Location(world, 1, 1, 1);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(false);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);

        FloorHitTrigger trigger = new FloorHitTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isTrue();
    }
}
