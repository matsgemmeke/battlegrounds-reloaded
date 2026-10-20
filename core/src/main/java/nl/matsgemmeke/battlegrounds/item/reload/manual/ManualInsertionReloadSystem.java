package nl.matsgemmeke.battlegrounds.item.reload.manual;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.reload.*;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ManualInsertionReloadSystem implements ReloadSystem {

    private final ReloadProperties properties;
    private final ResourceContainer resourceContainer;
    private final Scheduler scheduler;
    private final Set<Schedule> schedules;
    private final Set<SoundPlayback> soundPlaybacks;
    private final SoundEmitter soundEmitter;
    @Nullable
    private ReloadPerformer currentPerformer;

    @Inject
    public ManualInsertionReloadSystem(Scheduler scheduler, SoundEmitter soundEmitter, @Assisted ReloadProperties properties, @Assisted ResourceContainer resourceContainer) {
        this.scheduler = scheduler;
        this.soundEmitter = soundEmitter;
        this.properties = properties;
        this.resourceContainer = resourceContainer;
        this.schedules = new HashSet<>();
        this.soundPlaybacks = new HashSet<>();
    }

    @Override
    public boolean isPerforming() {
        return currentPerformer != null;
    }

    @Override
    public boolean performReload(ReloadPerformer performer, Procedure callback) {
        if (currentPerformer != null) {
            // Reload operation is still in progress, do not perform again
            return false;
        }

        currentPerformer = performer;
        performer.applyReloadingState();

        SoundSequence soundSequence = properties.soundSequence();
        long duration = properties.duration();

        if (soundSequence != null) {
            Schedule soundPlaySchedule = scheduler.createRepeatingSchedule(0, duration);
            soundPlaySchedule.addTask(() -> this.playSoundSequence(soundSequence, performer::getLocation));
            soundPlaySchedule.start();

            schedules.add(soundPlaySchedule);
        }

        Schedule reloadFinishSchedule = scheduler.createRepeatingSchedule(duration, duration);
        reloadFinishSchedule.addTask(() -> {
            this.addSingleAmmunition();
            callback.apply();
        });
        reloadFinishSchedule.start();

        schedules.add(reloadFinishSchedule);
        return true;
    }

    private void playSoundSequence(SoundSequence soundSequence, Supplier<Location> locationSupplier) {
        SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, locationSupplier);

        soundPlaybacks.add(soundPlayback);
    }

    @Override
    public boolean cancelReload() {
        if (currentPerformer == null) {
            return false;
        }

        schedules.forEach(Schedule::stop);
        schedules.clear();
        soundPlaybacks.forEach(SoundPlayback::cancel);
        soundPlaybacks.clear();
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
