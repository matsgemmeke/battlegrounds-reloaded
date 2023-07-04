package com.github.matsgemmeke.battlegrounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.jetbrains.annotations.NotNull;

public class SemiAutomaticMode implements FiringMode {

    private boolean readyToFire;
    @NotNull
    private Gun gun;
    private long cooldown;
    @NotNull
    private TaskRunner taskRunner;

    public SemiAutomaticMode(@NotNull TaskRunner taskRunner, @NotNull Gun gun, long cooldown) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.cooldown = cooldown;
        this.readyToFire = true;
    }

    public void activate() {
        if (!readyToFire) {
            return;
        }

        gun.shoot();

        readyToFire = false;

        taskRunner.runTaskLater(() -> readyToFire = true, cooldown);
    }
}
