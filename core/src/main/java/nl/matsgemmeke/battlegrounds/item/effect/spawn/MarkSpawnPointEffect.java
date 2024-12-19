package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.jetbrains.annotations.NotNull;

public class MarkSpawnPointEffect implements ItemEffect {

    @NotNull
    private SpawnPointProvider spawnPointProvider;

    public MarkSpawnPointEffect(@NotNull SpawnPointProvider spawnPointProvider) {
        this.spawnPointProvider = spawnPointProvider;
    }

    public void activate(@NotNull ItemEffectContext context) {
        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.getSource());

        spawnPointProvider.setCustomSpawnPoint(context.getHolder(), spawnPoint);
    }
}
