package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.BaseEffect;
import nl.matsgemmeke.battlegrounds.item.effect.EffectContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MarkSpawnPointEffect extends BaseEffect {

    @NotNull
    private final SpawnPointProvider spawnPointProvider;

    public MarkSpawnPointEffect(@NotNull SpawnPointProvider spawnPointProvider) {
        this.spawnPointProvider = spawnPointProvider;
    }

    public void perform(@NotNull EffectContext context) {
        Entity entity = context.getEntity();
        Location initiationLocation = context.getInitiationLocation();
        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.getSource(), initiationLocation.getYaw());

        spawnPointProvider.setCustomSpawnPoint(entity.getUniqueId(), spawnPoint);
    }

    public void reset() {
        if (currentContext == null) {
            return;
        }

        spawnPointProvider.setCustomSpawnPoint(currentContext.getEntity().getUniqueId(), null);
    }
}
