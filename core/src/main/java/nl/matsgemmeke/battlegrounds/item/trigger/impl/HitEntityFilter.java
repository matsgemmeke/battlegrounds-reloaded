package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Acts a filter for ray trace results, so that the trigger source and actor will not interrupt the ray.
 */
public class HitEntityFilter implements Predicate<Entity> {

    private final TriggerContext triggerContext;

    public HitEntityFilter(TriggerContext triggerContext) {
        this.triggerContext = triggerContext;
    }

    @Override
    public boolean test(Entity entity) {
        UUID sourceId = triggerContext.sourceId();
        Location actorLocation = triggerContext.actor().getLocation();

        // Ignore entities that overlap the ray start position (e.g. when the actor itself is an entity), otherwise
        // the ray would immediately collide with itself.
        return !entity.getUniqueId().equals(sourceId) && !entity.getLocation().equals(actorLocation);
    }
}
