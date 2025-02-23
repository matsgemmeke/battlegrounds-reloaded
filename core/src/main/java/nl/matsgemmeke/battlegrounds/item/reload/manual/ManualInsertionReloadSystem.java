package nl.matsgemmeke.battlegrounds.item.reload.manual;

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

public class ManualInsertionReloadSystem implements ReloadSystem {

    @NotNull
    private final AmmunitionHolder ammunitionHolder;
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
            @NotNull TaskRunner taskRunner,
            @Assisted @NotNull ReloadProperties properties,
            @Assisted @NotNull AmmunitionHolder ammunitionHolder,
            @Assisted @NotNull AudioEmitter audioEmitter
    ) {
        this.taskRunner = taskRunner;
        this.properties = properties;
        this.ammunitionHolder = ammunitionHolder;
        this.audioEmitter = audioEmitter;
        this.tasks = new ArrayList<>();
    }

    public boolean isPerforming() {
        return currentPerformer != null;
    }

    public boolean performReload(@NotNull ReloadPerformer performer) {
        currentPerformer = performer;
        performer.applyReloadingState();

        for (GameSound sound : properties.reloadSounds()) {
            tasks.add(taskRunner.runTaskTimer(() -> audioEmitter.playSound(sound, performer.getAudioPlayLocation()), sound.getDelay(), properties.duration()));
        }

        tasks.add(taskRunner.runTaskTimer(this::addSingleAmmunition, properties.duration(), properties.duration()));
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
        ammunitionHolder.setMagazineAmmo(ammunitionHolder.getMagazineAmmo() + 1);
        ammunitionHolder.setReserveAmmo(ammunitionHolder.getReserveAmmo() - 1);
        ammunitionHolder.updateAmmoDisplay();

        if (ammunitionHolder.getMagazineAmmo() >= ammunitionHolder.getMagazineSize() || ammunitionHolder.getReserveAmmo() <= 0) {
            this.cancelReload();
        }
    }
}
