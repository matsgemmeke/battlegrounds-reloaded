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
    private UUID uniqueId;

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
        uniqueId = context.getDamageSource().getUniqueId();
        Location initiationLocation = context.getInitiationLocation();

        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.getEffectSource(), initiationLocation.getYaw());

        spawnPointRegistry.setCustomSpawnPoint(uniqueId, spawnPoint);
    }

    @Override
    public void rollback() {
        if (uniqueId != null) {
            spawnPointRegistry.setCustomSpawnPoint(uniqueId, null);
        }
    }
}
