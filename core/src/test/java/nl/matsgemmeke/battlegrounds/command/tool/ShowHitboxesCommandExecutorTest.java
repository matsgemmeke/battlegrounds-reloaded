package nl.matsgemmeke.battlegrounds.command.tool;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowHitboxesCommandExecutorTest {

    private static final int SECONDS = 10;
    private static final double RANGE = 20.0;
    private static final String TOOL_HITBOX_SUCCESS_MESSAGE = "Displaying hitboxes for %bg_seconds% seconds inside a range of %bg_range% blocks.";

    @Mock
    private HitboxResolver hitboxResolver;
    @Mock
    private Scheduler scheduler;
    @Mock
    private Translator translator;
    @InjectMocks
    private ShowHitboxesCommandExecutor commandExecutor;

    @ParameterizedTest
    @CsvSource({
            "0.2,0.2,0.2,0.0",
            "0.2,0.2,0.2,22.5",
            "0.2,0.2,0.2,45.0"
    })
    @DisplayName("execute starts schedule that shows hitboxes of nearby entities for the given amount of seconds")
    void execute(double hitboxHeight, double hitboxWidth, double hitboxDepth, float yaw) {
        Location baseLocation = new Location(null, 10.0, 10.0, 10.0, yaw, 0.0f);
        Location playerLocation = new Location(null, 0, 0, 0);
        HitboxComponent component = new HitboxComponent(HitboxComponentType.TORSO, hitboxWidth, hitboxHeight, hitboxDepth, 0, 0, 0);
        RelativeHitbox relativeHitbox = new RelativeHitbox(Set.of(component));
        Hitbox hitbox = new Hitbox(baseLocation, relativeHitbox);
        Entity entity = mock(Entity.class);
        Schedule schedule = mock(Schedule.class);

        World world = mock(World.class);
        when(world.getNearbyEntities(playerLocation, RANGE, RANGE, RANGE)).thenReturn(List.of(entity));

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerLocation);
        when(player.getWorld()).thenReturn(world);

        HitboxProvider<Entity> hitboxProvider = mock();
        when(hitboxProvider.provideHitbox(entity)).thenReturn(hitbox);

        when(hitboxResolver.resolveHitboxProvider(entity)).thenReturn(hitboxProvider);
        when(scheduler.createRepeatingSchedule(0L, 1L, 200L)).thenReturn(schedule);
        when(translator.translate(TranslationKey.TOOL_HITBOX_SUCCESS.getPath())).thenReturn(new TextTemplate(TOOL_HITBOX_SUCCESS_MESSAGE));

        doAnswer(invocation -> {
            ScheduleTask task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(schedule).addTask(any(ScheduleTask.class));

        commandExecutor.execute(player, SECONDS, RANGE);

        verify(world, times(24)).spawnParticle(any(Particle.class), any(Location.class), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(DustOptions.class));
        verify(player).sendMessage("Displaying hitboxes for 10 seconds inside a range of 20.0 blocks.");
    }
}
