package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.reload.*;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class MagazineReloadSystem implements ReloadSystem {

    private final ReloadProperties properties;
    private final ResourceContainer resourceContainer;
    private final Scheduler scheduler;
    private final Set<Schedule> schedules;
    private final Set<SoundPlayback> soundPlaybacks;
    private final SoundEmitter soundEmitter;
    @Nullable
    private ReloadPerformer currentPerformer;

    @Inject
    public MagazineReloadSystem(Scheduler scheduler, SoundEmitter soundEmitter, @Assisted ReloadProperties properties, @Assisted ResourceContainer resourceContainer) {
        this.scheduler = scheduler;
        this.soundEmitter = soundEmitter;
        this.resourceContainer = resourceContainer;
        this.properties = properties;
        this.schedules = new HashSet<>();
        this.soundPlaybacks = new HashSet<>();
    }

    @Override
    public boolean isPerforming() {
        return currentPerformer != null;
    }

    @Override
    public boolean performReload(ReloadPerformer performer, Procedure callback) {
        currentPerformer = performer;
        performer.applyReloadingState();

        SoundSequence soundSequence = properties.soundSequence();

        if (soundSequence != null) {
            SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, performer::getLocation);

            soundPlaybacks.add(soundPlayback);
        }

        Schedule schedule = scheduler.createSingleRunSchedule(properties.duration());
        schedule.addTask(() -> {
            this.finalizeReload(performer);
            callback.apply();
        });
        schedule.start();

        schedules.add(schedule);
        return true;
    }

    @Override
    public boolean cancelReload() {
        if (currentPerformer == null) {
            return false;
        }

        soundPlaybacks.forEach(SoundPlayback::cancel);
        soundPlaybacks.clear();
        schedules.forEach(Schedule::stop);
        schedules.clear();
        currentPerformer.resetReloadingState();
        currentPerformer = null;
        return true;
    }

    private void finalizeReload(ReloadPerformer performer) {
        int loadedAmount = resourceContainer.getLoadedAmount();
        int capacity = resourceContainer.getCapacity();
        int reserveAmount = resourceContainer.getReserveAmount();
        int remainingCapacity = capacity - loadedAmount;

        if (reserveAmount > remainingCapacity) {
            resourceContainer.setReserveAmount(reserveAmount - remainingCapacity);
            resourceContainer.setLoadedAmount(capacity);
        } else {
            resourceContainer.setLoadedAmount(loadedAmount + reserveAmount);
            resourceContainer.setReserveAmount(0);
        }

        performer.resetReloadingState();

        currentPerformer = null;
    }
}
