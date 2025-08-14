package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MarkSpawnPointEffect extends BaseItemEffect {

    @NotNull
    private final SpawnPointRegistry spawnPointRegistry;

    @Inject
    public MarkSpawnPointEffect(@NotNull SpawnPointRegistry spawnPointRegistry) {
        this.spawnPointRegistry = spawnPointRegistry;
    }

    public void perform(@NotNull ItemEffectContext context) {
        UUID entityId = context.getEntity().getUniqueId();
        Location initiationLocation = context.getInitiationLocation();

        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.getSource(), initiationLocation.getYaw());

        spawnPointRegistry.setCustomSpawnPoint(entityId, spawnPoint);
    }

    public void reset() {
        if (currentContext == null) {
            return;
        }

        spawnPointRegistry.setCustomSpawnPoint(currentContext.getEntity().getUniqueId(), null);
    }
}
