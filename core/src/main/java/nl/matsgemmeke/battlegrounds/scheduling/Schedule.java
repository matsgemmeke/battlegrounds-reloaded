package nl.matsgemmeke.battlegrounds.scheduling;

public interface Schedule {

    void addTask(ScheduleTask task);

    void clearTasks();

    boolean isRunning();

    void start();

    void stop();
}
