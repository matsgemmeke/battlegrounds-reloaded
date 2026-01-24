package nl.matsgemmeke.battlegrounds.item.trigger.floor;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.BlockTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
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
    private Actor actor;

    private FloorHitTrigger trigger;

    @BeforeEach
    void setUp() {
        trigger = new FloorHitTrigger();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenActorNotExist() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        when(actor.exists()).thenReturn(false);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenBlockBelowActorIsPassable() {
        World world = mock(World.class);
        Location actorLocation = new Location(world, 1, 1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(true);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isFalse();
    }

    @Test
    void checkReturnsBlockTriggerResultWhenBlockBelowActorIsNotPassable() {
        World world = mock(World.class);
        Location actorLocation = new Location(world, 1, 1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(false);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);

        TriggerResult triggerResult = trigger.check(triggerContext);

        assertThat(triggerResult.activates()).isTrue();
        assertThat(triggerResult).isInstanceOfSatisfying(BlockTriggerResult.class, blockTriggerResult -> {
           assertThat(blockTriggerResult.getHitBlock()).isEqualTo(blockBelowObject);
           assertThat(blockTriggerResult.getHitLocation()).isEqualTo(actorLocation);
        });
    }
}
