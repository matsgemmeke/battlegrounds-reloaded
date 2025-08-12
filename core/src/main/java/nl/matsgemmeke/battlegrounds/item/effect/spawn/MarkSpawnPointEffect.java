package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MarkSpawnPointEffect extends BaseItemEffect {

    @NotNull
    private final SpawnPointRegistry spawnPointRegistry;

    @Inject
    public MarkSpawnPointEffect(@NotNull SpawnPointRegistry spawnPointRegistry) {
        this.spawnPointRegistry = spawnPointRegistry;
    }

    public void perform(@NotNull ItemEffectContext context) {
        Entity entity = context.getEntity();
        Location initiationLocation = context.getInitiationLocation();
        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.getSource(), initiationLocation.getYaw());

        spawnPointRegistry.setCustomSpawnPoint(entity.getUniqueId(), spawnPoint);
    }

    public void reset() {
        if (currentContext == null) {
            return;
        }

        spawnPointRegistry.setCustomSpawnPoint(currentContext.getEntity().getUniqueId(), null);
    }
}
