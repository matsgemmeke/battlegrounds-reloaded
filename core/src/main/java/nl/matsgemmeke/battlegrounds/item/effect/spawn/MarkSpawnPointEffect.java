package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MarkSpawnPointEffect extends BaseItemEffect {

    @NotNull
    private SpawnPointProvider spawnPointProvider;

    public MarkSpawnPointEffect(@NotNull ItemEffectActivation effectActivation, @NotNull SpawnPointProvider spawnPointProvider) {
        super(effectActivation);
        this.spawnPointProvider = spawnPointProvider;
    }

    public void perform(@NotNull ItemEffectContext context) {
        Deployer deployer = context.getDeployer();
        Entity entity = context.getEntity();
        Location deployDirection = deployer.getDeployDirection();
        SpawnPoint spawnPoint = new MarkedSpawnPoint(context.getSource(), deployDirection.getYaw());

        spawnPointProvider.setCustomSpawnPoint(entity.getUniqueId(), spawnPoint);
    }

    public void reset() {
        if (currentContext == null) {
            return;
        }

        spawnPointProvider.setCustomSpawnPoint(currentContext.getEntity().getUniqueId(), null);
    }
}
