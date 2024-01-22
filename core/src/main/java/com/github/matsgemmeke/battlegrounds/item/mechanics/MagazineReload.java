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

public class MagazineReload implements ReloadSystem {

    @NotNull
    private Gun gun;
    private int duration;
    @NotNull
    private Iterable<GameSound> reloadSounds;
    @NotNull
    private List<BukkitTask> currentTasks;
    @NotNull
    private TaskRunner taskRunner;

    public MagazineReload(
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

        gun.setCurrentOperatingMode(this);

        holder.applyOperatingState(true);

        for (GameSound sound : reloadSounds) {
            currentTasks.add(taskRunner.runTaskLater(() -> {
                context.playSound(sound, holder.getEntity().getLocation());
            }, sound.getDelay()));
        }

        currentTasks.add(taskRunner.runTaskLater(() -> this.performReload(holder), duration));
        return true;
    }

    public void cancel(@NotNull ItemHolder holder) {
        for (BukkitTask task : currentTasks) {
            task.cancel();
        }

        currentTasks.clear();

        gun.setCurrentOperatingMode(null);

        holder.applyOperatingState(false);
    }

    public void performReload(@NotNull ItemHolder holder) {
        int magazineAmmo = gun.getMagazineAmmo();
        int magazineSize = gun.getMagazineSize();
        int magazineSpace = magazineSize - magazineAmmo;
        int reserveAmmo = gun.getReserveAmmo();

        if (reserveAmmo > magazineSpace) {
            gun.setReserveAmmo(reserveAmmo - magazineSpace);
            gun.setMagazineAmmo(magazineSize);
        } else {
            // In case the magazine cannot be filled completely, use the remaining ammo
            gun.setMagazineAmmo(magazineAmmo + reserveAmmo);
            gun.setReserveAmmo(0);
        }

        holder.applyOperatingState(false);

        gun.setCurrentOperatingMode(null);
        gun.update();
    }
}
