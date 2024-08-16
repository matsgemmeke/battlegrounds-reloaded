package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ManualInsertionReloadSystem implements ReloadSystem {

    @NotNull
    private AmmunitionHolder ammunitionHolder;
    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private Iterable<GameSound> reloadSounds;
    @NotNull
    private List<BukkitTask> soundPlayTasks;
    private long duration;
    @Nullable
    private ReloadPerformer currentPerformer;
    @NotNull
    private TaskRunner taskRunner;

    public ManualInsertionReloadSystem(
            @NotNull AmmunitionHolder ammunitionHolder,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            long duration) {
        this.ammunitionHolder = ammunitionHolder;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.duration = duration;
        this.soundPlayTasks = new ArrayList<>();
        this.reloadSounds = new HashSet<>();
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
            soundPlayTasks.add(taskRunner.runTaskTimer(() -> {
                audioEmitter.playSound(sound, performer.getAudioPlayLocation());
            }, sound.getDelay(), duration));
        }

        soundPlayTasks.add(taskRunner.runTaskTimer(this::addAmmunition, duration, duration));
        return true;
    }

    public boolean cancelReload() {
        if (currentPerformer == null) {
            return false;
        }

        for (BukkitTask task : soundPlayTasks) {
            task.cancel();
        }

        currentPerformer.resetReloadingState();
        currentPerformer = null;
        soundPlayTasks.clear();
        return true;
    }

    public void addAmmunition() {
        ammunitionHolder.setMagazineAmmo(ammunitionHolder.getMagazineAmmo() + 1);
        ammunitionHolder.setReserveAmmo(ammunitionHolder.getReserveAmmo() - 1);
        ammunitionHolder.updateAmmoDisplay();

        if (ammunitionHolder.getMagazineAmmo() >= ammunitionHolder.getMagazineSize() || ammunitionHolder.getReserveAmmo() <= 0) {
            this.cancelReload();
        }
    }
}
