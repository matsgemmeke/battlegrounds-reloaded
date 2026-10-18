package nl.matsgemmeke.battlegrounds.sound;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * A handle to a sound sequence playback started by {@link SoundEmitter}.
 *
 * <p>Cues without a delay are played immediately when the playback is started and can therefore not be cancelled. Cues
 * with a delay are scheduled, and this handle allows the caller to cancel those that have not played yet.
 *
 * <p>Callers that do not need to cancel the playback may safely ignore this handle. Holding on to it does not keep the
 * sound from playing.
 */
public class SoundPlayback {

    private final List<Schedule> schedules;

    public SoundPlayback() {
        this.schedules = new ArrayList<>();
    }

    void add(Schedule schedule) {
        schedules.add(schedule);
    }

    /**
     * Cancels all cues of this playback that have not been played yet.
     */
    public void cancel() {
        schedules.forEach(Schedule::stop);
    }
}
