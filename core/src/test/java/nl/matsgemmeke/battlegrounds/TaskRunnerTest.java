package nl.matsgemmeke.battlegrounds;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskRunnerTest {

    private Plugin plugin;

    @BeforeEach
    public void setUp() {
        plugin = mock(Plugin.class);
    }

    @Test
    public void runTaskLaterRunsGivenBukkitRunnableWithGivenArgs() {
        BukkitTask task = mock(BukkitTask.class);
        long delay = 10L;

        BukkitRunnable runnable = mock(BukkitRunnable.class);
        when(runnable.runTaskLater(plugin, delay)).thenReturn(task);

        TaskRunner taskRunner = new TaskRunner(plugin);
        taskRunner.runTaskLater(runnable, delay);

        verify(runnable).runTaskLater(plugin, delay);
    }

    @Test
    public void runTaskLaterRunsGivenRunnableInPluginScheduleWithGivenArgs() {
        BukkitTask task = mock(BukkitTask.class);
        Runnable runnable = () -> {};
        long delay = 10L;

        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(scheduler.runTaskLater(plugin, runnable, delay)).thenReturn(task);

        Server server = mock(Server.class);
        when(server.getScheduler()).thenReturn(scheduler);

        when(plugin.getServer()).thenReturn(server);

        TaskRunner taskRunner = new TaskRunner(plugin);
        BukkitTask result = taskRunner.runTaskLater(runnable, delay);

        assertEquals(task, result);

        verify(scheduler).runTaskLater(plugin, runnable, delay);
    }

    @Test
    public void runTaskTimerRunsGivenBukkitRunnableWithGivenArgs() {
        BukkitTask task = mock(BukkitTask.class);
        long delay = 10L;
        long period = 2L;

        BukkitRunnable runnable = mock(BukkitRunnable.class);
        when(runnable.runTaskTimer(plugin, delay, period)).thenReturn(task);

        TaskRunner taskRunner = new TaskRunner(plugin);
        taskRunner.runTaskTimer(runnable, delay, period);

        verify(runnable).runTaskTimer(plugin, delay, period);
    }

    @Test
    public void runTaskTimerRunsGivenRunnableInPluginScheduleWithGivenArgs() {

        BukkitTask task = mock(BukkitTask.class);
        Runnable runnable = () -> {};
        long delay = 10L;
        long period = 2L;

        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(scheduler.runTaskTimer(plugin, runnable, delay, period)).thenReturn(task);

        Server server = mock(Server.class);
        when(server.getScheduler()).thenReturn(scheduler);

        when(plugin.getServer()).thenReturn(server);

        TaskRunner taskRunner = new TaskRunner(plugin);
        BukkitTask result = taskRunner.runTaskTimer(runnable, delay, period);

        assertEquals(task, result);

        verify(scheduler).runTaskTimer(plugin, runnable, delay, period);
    }
}
