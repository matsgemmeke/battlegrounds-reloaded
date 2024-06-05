package nl.matsgemmeke.battlegrounds;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to run asynchronous tasks for the Battlegrounds plugin.
 */
public interface TaskRunner {

    @NotNull
    BukkitTask runTaskLater(@NotNull BukkitRunnable runnable, long delay);

    @NotNull
    BukkitTask runTaskLater(@NotNull Runnable runnable, long delay);

    @NotNull
    BukkitTask runTaskTimer(@NotNull BukkitRunnable runnable, long delay, long period);

    @NotNull
    BukkitTask runTaskTimer(@NotNull Runnable runnable, long delay, long period);
}
