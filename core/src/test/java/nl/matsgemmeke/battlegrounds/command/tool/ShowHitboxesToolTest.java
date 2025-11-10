package nl.matsgemmeke.battlegrounds.command.tool;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.resolver.HitboxResolver;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowHitboxesToolTest {

    private static final int SECONDS = 10;
    private static final double RANGE = 20.0;

    @Mock
    private HitboxResolver hitboxResolver;
    @Mock
    private Scheduler scheduler;
    @Mock
    private Translator translator;
    @InjectMocks
    private ShowHitboxesTool showHitboxesTool;

    @Test
    void executeStartsScheduleThatShowsHitboxesOfNearbyEntitiesForGivenAmountOfSeconds() {
        Location entityLocation = new Location(null, 10.0, 10.0, 10.0, 0.0f, 0.0f);
        Location playerLocation = new Location(null, 0, 0, 0);
        HitboxComponent component = new HitboxComponent(HitboxComponentType.TORSO, 0.2, 0.2, 0.2, 0, 0, 0);
        PositionHitbox positionHitbox = new PositionHitbox(Set.of(component));
        Schedule schedule = mock(Schedule.class);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(entityLocation);

        World world = mock(World.class);
        when(world.getNearbyEntities(playerLocation, RANGE, RANGE, RANGE)).thenReturn(List.of(entity));

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerLocation);
        when(player.getWorld()).thenReturn(world);

        Hitbox hitbox = mock(Hitbox.class);
        when(hitbox.getCurrentPositionHitbox()).thenReturn(positionHitbox);

        when(hitboxResolver.resolveHitbox(entity)).thenReturn(Optional.of(hitbox));
        when(scheduler.createRepeatingSchedule(0L, 1L, 200L)).thenReturn(schedule);
        when(translator.translate(TranslationKey.TOOL_HITBOX_SUCCESSFUL.getPath())).thenReturn(new TextTemplate("Displaying hitboxes for %bg_seconds% seconds inside a range of %bg_range% blocks."));

        doAnswer(invocation -> {
            ScheduleTask task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(schedule).addTask(any(ScheduleTask.class));

        showHitboxesTool.execute(player, SECONDS, RANGE);

        verify(world, times(24)).spawnParticle(any(Particle.class), any(Location.class), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(DustOptions.class));
        verify(player).sendMessage("Displaying hitboxes for 10 seconds inside a range of 20.0 blocks.");
    }
}
