package nl.matsgemmeke.battlegrounds.item.reload.manual;

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

public class ManualInsertionReloadSystem implements ReloadSystem {

    private final AudioEmitter audioEmitter;
    private final List<Schedule> schedules;
    private final ReloadProperties properties;
    private final ResourceContainer resourceContainer;
    private final Scheduler scheduler;
    @Nullable
    private ReloadPerformer currentPerformer;

    @Inject
    public ManualInsertionReloadSystem(AudioEmitter audioEmitter, Scheduler scheduler, @Assisted ReloadProperties properties, @Assisted ResourceContainer resourceContainer) {
        this.audioEmitter = audioEmitter;
        this.scheduler = scheduler;
        this.properties = properties;
        this.resourceContainer = resourceContainer;
        this.schedules = new ArrayList<>();
    }

    public boolean isPerforming() {
        return currentPerformer != null;
    }

    public boolean performReload(ReloadPerformer performer, Procedure callback) {
        currentPerformer = performer;
        performer.applyReloadingState();

        for (GameSound sound : properties.reloadSounds()) {
            Schedule soundPlaySchedule = scheduler.createRepeatingSchedule(sound.getDelay(), properties.duration());
            soundPlaySchedule.addTask(() -> audioEmitter.playSound(sound, performer.getLocation()));
            soundPlaySchedule.start();

            schedules.add(soundPlaySchedule);
        }

        Schedule reloadFinishSchedule = scheduler.createRepeatingSchedule(properties.duration(), properties.duration());
        reloadFinishSchedule.addTask(() -> {
            this.addSingleAmmunition();
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

    private void addSingleAmmunition() {
        resourceContainer.setLoadedAmount(resourceContainer.getLoadedAmount() + 1);
        resourceContainer.setReserveAmount(resourceContainer.getReserveAmount() - 1);

        if (resourceContainer.getLoadedAmount() >= resourceContainer.getCapacity() || resourceContainer.getReserveAmount() <= 0) {
            this.cancelReload();
        }
    }
}
