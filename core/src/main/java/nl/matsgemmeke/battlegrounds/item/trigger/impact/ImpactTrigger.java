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
    private boolean activated;

    public ImpactTrigger(@NotNull Schedule schedule) {
        this.schedule = schedule;
        this.activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate(@NotNull TriggerContext context) {
        schedule.addTask(() -> this.runCheck(context));
        schedule.start();

        activated = true;
    }

    private void runCheck(@NotNull TriggerContext context) {
        TriggerTarget target = context.target();

        if (!target.exists()) {
            this.deactivate();
            return;
        }

        Vector velocity = target.getVelocity();
        Location projectileLocation = target.getLocation();

        World world = target.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(projectileLocation, velocity, RAY_TRACE_MAX_DISTANCE);

        if (rayTraceResult == null
                || rayTraceResult.getHitBlock() == null
                || rayTraceResult.getHitBlockFace() == null
                || !rayTraceResult.getHitBlock().getType().isSolid()) {
            return;
        }

        this.notifyObservers();
        this.deactivate();
    }

    public void deactivate() {
        schedule.stop();
        activated = false;
    }
}
