package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.reload.*;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MagazineReloadSystem implements ReloadSystem {

    private final AudioEmitter audioEmitter;
    private final List<Schedule> schedules;
    private final ReloadProperties properties;
    private final ResourceContainer resourceContainer;
    private final Scheduler scheduler;
    @Nullable
    private ReloadPerformer currentPerformer;

    @Inject
    public MagazineReloadSystem(AudioEmitter audioEmitter, Scheduler scheduler, @Assisted ReloadProperties properties, @Assisted ResourceContainer resourceContainer) {
        this.audioEmitter = audioEmitter;
        this.scheduler = scheduler;
        this.resourceContainer = resourceContainer;
        this.properties = properties;
        this.schedules = new ArrayList<>();
    }

    public boolean isPerforming() {
        return currentPerformer != null;
    }

    public boolean performReload(ReloadPerformer performer, Procedure callback) {
        currentPerformer = performer;
        performer.applyReloadingState();

        for (GameSound sound : properties.reloadSounds()) {
            Schedule soundPlaySchedule = scheduler.createSingleRunSchedule(sound.getDelay());
            soundPlaySchedule.addTask(() -> audioEmitter.playSound(sound, performer.getLocation()));
            soundPlaySchedule.start();

            schedules.add(soundPlaySchedule);
        }

        Schedule reloadFinishSchedule = scheduler.createSingleRunSchedule(properties.duration());
        reloadFinishSchedule.addTask(() -> {
            this.finalizeReload(performer);
            callback.apply();
        });
        reloadFinishSchedule.start();

        schedules.add(reloadFinishSchedule);
        return true;
    }

    public boolean cancelReload() {
        if (currentPerformer == null) {
            return false;
        }

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
