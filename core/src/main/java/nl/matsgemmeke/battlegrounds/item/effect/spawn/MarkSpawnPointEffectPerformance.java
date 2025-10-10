package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;

import java.util.UUID;

public class MarkSpawnPointEffectPerformance extends BaseItemEffectPerformance {

    private final SpawnPointRegistry spawnPointRegistry;
    private UUID entityId;

    @Inject
    public MarkSpawnPointEffectPerformance(SpawnPointRegistry spawnPointRegistry) {
        this.spawnPointRegistry = spawnPointRegistry;
    }

    @Override
    public boolean isPerforming() {
        return false;
    }

    @Override
    public void perform(ItemEffectContext context) {
        entityId = context.entity().getUniqueId();
        Location initiationLocation = context.initiationLocation();

        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.source(), initiationLocation.getYaw());

        spawnPointRegistry.setCustomSpawnPoint(entityId, spawnPoint);
    }

    @Override
    public void rollback() {
        if (entityId != null) {
            spawnPointRegistry.setCustomSpawnPoint(entityId, null);
        }
    }
}
