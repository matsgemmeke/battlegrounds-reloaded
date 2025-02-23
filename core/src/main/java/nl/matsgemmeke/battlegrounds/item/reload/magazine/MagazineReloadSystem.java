package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MagazineReloadSystem implements ReloadSystem {

    @NotNull
    private final AmmunitionHolder ammunitionHolder;
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
            @Assisted @NotNull AmmunitionHolder ammunitionHolder,
            @Assisted @NotNull AudioEmitter audioEmitter
    ) {
        this.ammunitionHolder = ammunitionHolder;
        this.properties = properties;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.currentTasks = new ArrayList<>();
    }

    public boolean isPerforming() {
        return currentPerformer != null;
    }

    public boolean performReload(@NotNull ReloadPerformer performer) {
        currentPerformer = performer;
        performer.applyReloadingState();

        for (GameSound sound : properties.reloadSounds()) {
            currentTasks.add(taskRunner.runTaskLater(() -> audioEmitter.playSound(sound, performer.getAudioPlayLocation()), sound.getDelay()));
        }

        currentTasks.add(taskRunner.runTaskLater(() -> this.finalizeReload(performer), properties.duration()));
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
        int magazineAmmo = ammunitionHolder.getMagazineAmmo();
        int magazineSize = ammunitionHolder.getMagazineSize();
        int magazineSpace = magazineSize - magazineAmmo;
        int reserveAmmo = ammunitionHolder.getReserveAmmo();

        if (reserveAmmo > magazineSpace) {
            ammunitionHolder.setReserveAmmo(reserveAmmo - magazineSpace);
            ammunitionHolder.setMagazineAmmo(magazineSize);
        } else {
            // In case the magazine cannot be filled completely, use the remaining ammo
            ammunitionHolder.setMagazineAmmo(magazineAmmo + reserveAmmo);
            ammunitionHolder.setReserveAmmo(0);
        }

        performer.resetReloadingState();

        currentPerformer = null;

        ammunitionHolder.updateAmmoDisplay();
    }
}
