package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MarkSpawnPointEffectPerformance implements ItemEffectPerformance {

    private final Set<TriggerRun> triggerRuns;
    private final SpawnPointRegistry spawnPointRegistry;
    private UUID entityId;

    @Inject
    public MarkSpawnPointEffectPerformance(SpawnPointRegistry spawnPointRegistry) {
        this.spawnPointRegistry = spawnPointRegistry;
        this.triggerRuns = new HashSet<>();
    }

    @Override
    public void addTriggerRun(TriggerRun triggerRun) {
        triggerRuns.add(triggerRun);
    }

    @Override
    public boolean isPerforming() {
        return false;
    }

    @Override
    public void perform(ItemEffectContext context) {
        entityId = context.getEntity().getUniqueId();
        Location initiationLocation = context.getInitiationLocation();

        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.getSource(), initiationLocation.getYaw());

        spawnPointRegistry.setCustomSpawnPoint(entityId, spawnPoint);
    }

    @Override
    public void cancel() {
        if (entityId != null) {
            spawnPointRegistry.setCustomSpawnPoint(entityId, null);
        }

        triggerRuns.forEach(TriggerRun::cancel);
    }
}
