package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

public interface ProjectileLauncher {

    /**
     * Cancels any pending side effects associated with launching a projectile.
     *
     * <p>Launching a projectile may schedule various effects (such as sounds, particles, or other delayed actions) to
     * occur when a projectile is launched. Some of these effects may be scheduled to execute after a delay using task
     * schedulers. Calling this method will cancel all such pending or scheduled tasks that have not yet been executed.
     *
     * <p>This method does not affect the projectile itself once it has already been launched; any projectile currently
     * in motion or any effects that have already occurred will not be reverted or cancelled.
     */
    void cancel();

    void launch(LaunchContext context);
}
