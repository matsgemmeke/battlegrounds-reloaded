package com.github.matsgemmeke.battlegrounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import org.jetbrains.annotations.NotNull;

public class MagazineReload implements ReloadSystem {

    private boolean cancelled;
    @NotNull
    private Gun gun;
    private int duration;
    @NotNull
    private TaskRunner taskRunner;

    public MagazineReload(@NotNull TaskRunner taskRunner, @NotNull Gun gun, int duration) {
        this.taskRunner = taskRunner;
        this.gun = gun;
        this.duration = duration;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void activate() {
        taskRunner.runTaskLater(this::performReload, duration);
    }

    private void performReload() {

    }
}
