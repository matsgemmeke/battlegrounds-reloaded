package com.github.matsgemmeke.battlegrounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import org.jetbrains.annotations.NotNull;

public class SemiAutomaticMode implements FiringMode {

    private boolean readyToFire;
    @NotNull
    private Firearm firearm;
    private long cooldown;
    @NotNull
    private TaskRunner taskRunner;

    public SemiAutomaticMode(@NotNull TaskRunner taskRunner, @NotNull Firearm firearm, long cooldown) {
        this.taskRunner = taskRunner;
        this.firearm = firearm;
        this.cooldown = cooldown;
        this.readyToFire = true;
    }

    public void activate() {
        if (!readyToFire) {
            return;
        }

        firearm.shoot();

        readyToFire = false;

        taskRunner.runTaskLater(() -> readyToFire = true, cooldown);
    }
}
