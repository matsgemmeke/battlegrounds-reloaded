package nl.matsgemmeke.battlegrounds.sound;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SoundPlaybackTest {

    @Test
    @DisplayName("cancel cancels all added schedules")
    void cancel() {
        Schedule schedule = mock(Schedule.class);

        SoundPlayback soundPlayback = new SoundPlayback();
        soundPlayback.add(schedule);
        soundPlayback.cancel();

        verify(schedule).stop();
    }
}
