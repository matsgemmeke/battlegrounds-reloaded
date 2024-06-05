package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.audio.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MagazineReloadSystem implements ReloadSystem {

    @NotNull
    private AmmunitionHolder ammunitionHolder;
    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private Iterable<GameSound> reloadSounds;
    @NotNull
    private List<BukkitTask> currentTasks;
    private long duration;
    @Nullable
    private ReloadPerformer currentPerformer;
    @NotNull
    private TaskRunner taskRunner;

    public MagazineReloadSystem(
            @NotNull AmmunitionHolder ammunitionHolder,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            long duration
    ) {
        this.ammunitionHolder = ammunitionHolder;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.duration = duration;
        this.currentTasks = new ArrayList<>();
        this.reloadSounds = Collections.emptySet();
    }

    @NotNull
    public Iterable<GameSound> getReloadSounds() {
        return reloadSounds;
    }

    public void setReloadSounds(@NotNull Iterable<GameSound> reloadSounds) {
        this.reloadSounds = reloadSounds;
    }

    public boolean isPerforming() {
        return currentPerformer != null;
    }

    public boolean performReload(@NotNull ReloadPerformer performer) {
        currentPerformer = performer;
        performer.applyReloadingState();

        for (GameSound sound : reloadSounds) {
            currentTasks.add(taskRunner.runTaskLater(() -> {
                audioEmitter.playSound(sound, performer.getAudioPlayLocation());
            }, sound.getDelay()));
        }

        currentTasks.add(taskRunner.runTaskLater(() -> this.finalizeReload(performer), duration));
        return true;
    }

    public boolean cancel() {
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

    public void finalizeReload(@NotNull ReloadPerformer performer) {
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
