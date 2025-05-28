package nl.matsgemmeke.battlegrounds.item.trigger.impact;

import nl.matsgemmeke.battlegrounds.item.trigger.BaseTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ImpactTrigger extends BaseTrigger {

    private static final double RAY_TRACE_MAX_DISTANCE = 1.0;

    @NotNull
    private final Schedule schedule;
    private boolean started;

    public ImpactTrigger(@NotNull Schedule schedule) {
        this.schedule = schedule;
        this.started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public void start(@NotNull TriggerContext context) {
        schedule.addTask(() -> this.runCheck(context));
        schedule.start();
        started = true;
    }

    private void runCheck(@NotNull TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            this.stop();
            return;
        }

        Vector velocity = target.getVelocity();
        Location projectileLocation = target.getLocation();

        // Stop the current check if the projectile does not move, because we cannot cast a ray trace with zero magnitude
        if (velocity.isZero()) {
            return;
        }

        World world = target.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(projectileLocation, velocity, RAY_TRACE_MAX_DISTANCE);

        if (rayTraceResult == null
                || rayTraceResult.getHitBlock() == null
                || rayTraceResult.getHitBlockFace() == null
                || !rayTraceResult.getHitBlock().getType().isSolid()) {
            return;
        }

        this.notifyObservers();
    }

    public void stop() {
        schedule.stop();
        started = false;
    }
}
