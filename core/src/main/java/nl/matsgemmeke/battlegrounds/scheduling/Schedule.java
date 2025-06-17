package nl.matsgemmeke.battlegrounds.scheduling;

import org.jetbrains.annotations.NotNull;

public interface Schedule {

    void addTask(@NotNull ScheduleTask task);

    void clearTasks();

    boolean isRunning();

    void start();

    void stop();
}
