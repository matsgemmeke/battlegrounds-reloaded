package com.github.matsgemmeke.battlegrounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.jetbrains.annotations.NotNull;

public class SemiAutomaticMode implements FireMode {

    private boolean coolingDown;
    @NotNull
    private Gun gun;
    private long cooldownDuration;
    @NotNull
    private TaskRunner taskRunner;

    public SemiAutomaticMode(@NotNull TaskRunner taskRunner, @NotNull Gun gun, long cooldownDuration) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.cooldownDuration = cooldownDuration;
        this.coolingDown = false;
    }

    public boolean activate(@NotNull ItemHolder holder) {
        if (coolingDown) {
            return false;
        }

        coolingDown = true;
        gun.shoot();
        taskRunner.runTaskLater(() -> this.cancel(holder), cooldownDuration);
        return true;
    }

    public void cancel(@NotNull ItemHolder holder) {
        coolingDown = false;
    }
}
