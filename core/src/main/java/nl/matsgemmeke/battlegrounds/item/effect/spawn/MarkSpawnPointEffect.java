package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class MarkSpawnPointEffect extends BaseItemEffect {

    @NotNull
    private SpawnPointProvider spawnPointProvider;

    public MarkSpawnPointEffect(@NotNull ItemEffectActivation effectActivation, @NotNull SpawnPointProvider spawnPointProvider) {
        super(effectActivation);
        this.spawnPointProvider = spawnPointProvider;
    }

    public void cancel() {
        if (currentContext == null) {
            return;
        }

        spawnPointProvider.setCustomSpawnPoint(currentContext.getHolder(), null);
    }

    public void perform(@NotNull ItemEffectContext context) {
        Location eyeLocation = context.getHolder().getEntity().getEyeLocation();
        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.getSource(), eyeLocation.getYaw());

        spawnPointProvider.setCustomSpawnPoint(context.getHolder(), spawnPoint);
    }
}
