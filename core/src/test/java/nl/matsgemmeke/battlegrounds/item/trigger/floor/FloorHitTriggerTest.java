package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FloorHitTriggerTest {

    private static final UUID SOURCE_ID = UUID.randomUUID();

    @Mock
    private TriggerTarget target;

    private FloorHitTrigger trigger;

    @BeforeEach
    void setUp() {
        trigger = new FloorHitTrigger();
    }

    @Test
    void activatesReturnsFalseWhenTargetDoesNotExists() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(false);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenBlockBelowTargetIsPassable() {
        World world = mock(World.class);
        Location targetLocation = new Location(world, 1, 1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(true);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);

        FloorHitTrigger trigger = new FloorHitTrigger();
        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsTrueWhenBlockBelowTargetIsNotPassable() {
        World world = mock(World.class);
        Location targetLocation = new Location(world, 1, 1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(false);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);

        FloorHitTrigger trigger = new FloorHitTrigger();
        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isTrue();
    }
}
