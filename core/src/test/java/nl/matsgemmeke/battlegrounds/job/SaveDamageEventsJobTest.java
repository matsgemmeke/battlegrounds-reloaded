package nl.matsgemmeke.battlegrounds.job;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEventTracker;
import nl.matsgemmeke.battlegrounds.scheduling.TaskRunner;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveDamageEventsJobTest {

    @Mock
    private DamageEventTracker damageEventTracker;
    @Mock
    private Logger logger;
    @Mock
    private TaskRunner taskRunner;
    @InjectMocks
    private SaveDamageEventsJob job;

    @Test
    @DisplayName("run saves all damage events in DamageEventTracker in a separate thread")
    void run() {
        DamageEvent damageEvent = new DamageEvent(null, null, null, 0, null, 0, false, false, null);

        when(damageEventTracker.saveAll()).thenReturn(List.of(damageEvent));

        doAnswer(MockUtils.answerRunRunnable()).when(taskRunner).runTaskAsynchronously(any(Runnable.class));

        job.run();

        verify(damageEventTracker).saveAll();
        verify(logger).info("[Battlegrounds] Saved 1 damage event to the database");
    }
}
