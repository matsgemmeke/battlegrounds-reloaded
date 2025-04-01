package nl.matsgemmeke.battlegrounds.item.reload.magazine;

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

public class MagazineReloadSystem implements ReloadSystem {

    @NotNull
    private final AmmunitionStorage ammunitionStorage;
    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final List<BukkitTask> currentTasks;
    @Nullable
    private ReloadPerformer currentPerformer;
    @NotNull
    private final ReloadProperties properties;
    @NotNull
    private final TaskRunner taskRunner;

    @Inject
    public MagazineReloadSystem(
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull ReloadProperties properties,
            @Assisted @NotNull AmmunitionStorage ammunitionStorage,
            @Assisted @NotNull AudioEmitter audioEmitter
    ) {
        this.ammunitionStorage = ammunitionStorage;
        this.properties = properties;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.currentTasks = new ArrayList<>();
    }

    public boolean isPerforming() {
        return currentPerformer != null;
    }

    public boolean performReload(@NotNull ReloadPerformer performer, @NotNull Procedure callback) {
        currentPerformer = performer;
        performer.applyReloadingState();

        for (GameSound sound : properties.reloadSounds()) {
            currentTasks.add(taskRunner.runTaskLater(() -> audioEmitter.playSound(sound, performer.getLocation()), sound.getDelay()));
        }

        currentTasks.add(taskRunner.runTaskLater(() -> {
            this.finalizeReload(performer);
            callback.apply();
        }, properties.duration()));

        return true;
    }

    public boolean cancelReload() {
        if (currentPerformer == null) {
            return false;
        }

        for (BukkitTask task : currentTasks) {
            task.cancel();
        }

        currentPerformer.resetReloadingState();
        currentPerformer = null;
        currentTasks.clear();
        return true;
    }

    private void finalizeReload(@NotNull ReloadPerformer performer) {
        int magazineAmmo = ammunitionStorage.getMagazineAmmo();
        int magazineSize = ammunitionStorage.getMagazineSize();
        int magazineSpace = magazineSize - magazineAmmo;
        int reserveAmmo = ammunitionStorage.getReserveAmmo();

        if (reserveAmmo > magazineSpace) {
            ammunitionStorage.setReserveAmmo(reserveAmmo - magazineSpace);
            ammunitionStorage.setMagazineAmmo(magazineSize);
        } else {
            // In case the magazine cannot be filled completely, use the remaining ammo
            ammunitionStorage.setMagazineAmmo(magazineAmmo + reserveAmmo);
            ammunitionStorage.setReserveAmmo(0);
        }

        performer.resetReloadingState();

        currentPerformer = null;
    }
}
