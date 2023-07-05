package com.github.matsgemmeke.battlegrounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
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
    private Iterable<BattleSound> reloadSounds;
    @NotNull
    private List<BukkitTask> currentTasks;
    @NotNull
    private TaskRunner taskRunner;

    public MagazineReload(
            @NotNull TaskRunner taskRunner,
            @NotNull Gun gun,
            @NotNull Iterable<BattleSound> reloadSounds,
            int duration
    ) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.reloadSounds = reloadSounds;
        this.duration = duration;
        this.currentTasks = new ArrayList<>();
    }

    public boolean activate(@NotNull BattleItemHolder holder) {
        BattleContext context = gun.getContext();

        gun.setReloading(true);

        for (BattleSound sound : reloadSounds) {
            currentTasks.add(taskRunner.runTaskLater(() -> {
                context.playSound(sound, holder.getEntity().getLocation());
            }, sound.getDelay()));
        }

        currentTasks.add(taskRunner.runTaskLater(this::performReload, duration));
        return true;
    }

    public void cancel() {
        for (BukkitTask task : currentTasks) {
            task.cancel();
        }

        currentTasks.clear();

        gun.setReloading(false);
    }

    public void performReload() {
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

        gun.setReloading(false);
        gun.update();
    }
}
