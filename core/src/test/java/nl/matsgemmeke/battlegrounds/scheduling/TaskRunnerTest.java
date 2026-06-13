package nl.matsgemmeke.battlegrounds.scheduling;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskRunnerTest {

    @Mock
    private BukkitScheduler bukkitScheduler;
    @Mock
    private Plugin plugin;
    @InjectMocks
    private TaskRunner taskRunner;

    @Test
    @DisplayName("runTaskAsynchronously performs an asynchronous task on the bukkit scheduler")
    void runTaskAsynchronously() {
        Runnable runnable = mock(Runnable.class);

        taskRunner.runTaskAsynchronously(runnable);

        verify(bukkitScheduler).runTaskAsynchronously(plugin, runnable);
    }
}
