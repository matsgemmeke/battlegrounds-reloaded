package nl.matsgemmeke.battlegrounds.job;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.fixture.DamageEventFixture;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEventTracker;
import nl.matsgemmeke.battlegrounds.scheduling.TaskRunner;
import nl.matsgemmeke.battlegrounds.storage.stats.StatsStorage;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveDamageEventsJobTest {

    @Mock
    private DamageEventTracker damageEventTracker;
    @Mock
    private Logger logger;
    @Mock
    private StatsStorage statsStorage;
    @Mock
    private TaskRunner taskRunner;
    @InjectMocks
    private SaveDamageEventsJob job;
    @Captor
    private ArgumentCaptor<List<DamageEvent>> savedDamageEventsCaptor;

    @Test
    @DisplayName("run saves all damage events in DamageEventTracker in a separate thread")
    void run() {
        DamageEvent damageEvent = DamageEventFixture.createDefault();

        when(damageEventTracker.getTrackedDamageEvents()).thenReturn(List.of(damageEvent));

        doAnswer(MockUtils.answerRunRunnable()).when(taskRunner).runTaskAsynchronously(any(Runnable.class));

        job.run();

        verify(statsStorage).saveDamageEvents(savedDamageEventsCaptor.capture());

        assertThat(savedDamageEventsCaptor.getValue()).containsExactly(damageEvent);

        verify(logger).info("Saved 1 damage event to the database");
    }
}
