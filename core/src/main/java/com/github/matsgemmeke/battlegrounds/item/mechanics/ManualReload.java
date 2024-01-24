package com.github.matsgemmeke.battlegrounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.game.GameSound;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ManualReload implements ReloadSystem {

    @NotNull
    private Gun gun;
    private int duration;
    @NotNull
    private Iterable<GameSound> reloadSounds;
    @NotNull
    private List<BukkitTask> currentTasks;
    @NotNull
    private TaskRunner taskRunner;

    public ManualReload(
            @NotNull TaskRunner taskRunner,
            @NotNull Gun gun,
            @NotNull Iterable<GameSound> reloadSounds,
            int duration
    ) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.reloadSounds = reloadSounds;
        this.duration = duration;
        this.currentTasks = new ArrayList<>();
    }

    public boolean activate(@NotNull ItemHolder holder) {
        GameContext context = gun.getContext();

        gun.setOperatingMode(this);

        holder.applyOperatingState(true);

        for (GameSound sound : reloadSounds) {
            currentTasks.add(taskRunner.runTaskTimer(() -> {
                context.playSound(sound, holder.getEntity().getLocation());
            }, sound.getDelay(), duration));
        }

        currentTasks.add(taskRunner.runTaskTimer(() -> this.performReload(holder), duration, duration));
        return true;
    }

    public void cancel(@NotNull ItemHolder holder) {
        for (BukkitTask task : currentTasks) {
            task.cancel();
        }

        currentTasks.clear();

        gun.setOperatingMode(null);

        holder.applyOperatingState(false);
    }

    public void performReload(@NotNull ItemHolder holder) {
        gun.setMagazineAmmo(gun.getMagazineAmmo() + 1);
        gun.setReserveAmmo(gun.getReserveAmmo() - 1);
        gun.update();

        if (gun.getMagazineAmmo() >= gun.getMagazineSize() || gun.getReserveAmmo() <= 0) {
            this.cancel(holder);
        }
    }
}
