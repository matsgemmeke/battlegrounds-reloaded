package nl.matsgemmeke.battlegrounds.scheduling;

import org.jetbrains.annotations.NotNull;

public interface Schedule {

    void addTask(@NotNull ScheduleTask task);

    boolean isRunning();

    void start();

    void stop();
}
