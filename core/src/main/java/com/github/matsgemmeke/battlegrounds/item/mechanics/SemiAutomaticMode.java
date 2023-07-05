package com.github.matsgemmeke.battlegrounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SemiAutomaticMode implements FireMode {

    @Nullable
    private BukkitTask cooldownTask;
    @NotNull
    private Gun gun;
    private long cooldown;
    @NotNull
    private TaskRunner taskRunner;

    public SemiAutomaticMode(@NotNull TaskRunner taskRunner, @NotNull Gun gun, long cooldown) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.cooldown = cooldown;
    }

    public boolean activate(@NotNull BattleItemHolder holder) {
        gun.setCurrentOperatingMode(this);
        gun.shoot();

        cooldownTask = taskRunner.runTaskLater(this::cancel, cooldown);
        return true;
    }

    public void cancel() {
        gun.setCurrentOperatingMode(null);

        if (cooldownTask == null) {
            return;
        }

        cooldownTask.cancel();
    }
}
