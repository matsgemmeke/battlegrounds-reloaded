package nl.matsgemmeke.battlegrounds.item.reload.manual;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ManualInsertionReloadSystem implements ReloadSystem {

    @NotNull
    private final AmmunitionStorage ammunitionStorage;
    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final List<BukkitTask> tasks;
    @Nullable
    private ReloadPerformer currentPerformer;
    @NotNull
    private final ReloadProperties properties;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public ManualInsertionReloadSystem(
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull ReloadProperties properties,
            @Assisted @NotNull AmmunitionStorage ammunitionStorage
    ) {
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.ammunitionStorage = ammunitionStorage;
        this.audioEmitter = audioEmitter;
        this.tasks = new ArrayList<>();
    }

    public boolean isPerforming() {
        return currentPerformer != null;
    }

    public boolean performReload(@NotNull ReloadPerformer performer, @NotNull Procedure callback) {
        currentPerformer = performer;
        performer.applyReloadingState();

        for (GameSound sound : properties.reloadSounds()) {
            tasks.add(taskRunner.runTaskTimer(() -> audioEmitter.playSound(sound, performer.getLocation()), sound.getDelay(), properties.duration()));
        }

        tasks.add(taskRunner.runTaskTimer(() -> {
            this.addSingleAmmunition();
            callback.apply();
        }, properties.duration(), properties.duration()));

        return true;
    }

    public boolean cancelReload() {
        if (currentPerformer == null) {
            return false;
        }

        for (BukkitTask task : tasks) {
            task.cancel();
        }

        currentPerformer.resetReloadingState();
        currentPerformer = null;
        tasks.clear();
        return true;
    }

    public void addSingleAmmunition() {
        ammunitionStorage.setMagazineAmmo(ammunitionStorage.getMagazineAmmo() + 1);
        ammunitionStorage.setReserveAmmo(ammunitionStorage.getReserveAmmo() - 1);

        if (ammunitionStorage.getMagazineAmmo() >= ammunitionStorage.getMagazineSize() || ammunitionStorage.getReserveAmmo() <= 0) {
            this.cancelReload();
        }
    }
}
