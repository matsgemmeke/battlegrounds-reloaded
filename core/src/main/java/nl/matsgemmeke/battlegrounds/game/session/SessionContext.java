package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import org.jetbrains.annotations.NotNull;

public class SessionContext implements GameContext {

    @NotNull
    public ActionHandler getActionHandler() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public AudioEmitter getAudioEmitter() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public CollisionDetector getCollisionDetector() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public DamageProcessor getDamageProcessor() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public DeploymentInfoProvider getDeploymentInfoProvider() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public EquipmentRegistry getEquipmentRegistry() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public GunInfoProvider getGunInfoProvider() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public GunRegistry getGunRegistry() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public PlayerRegistry getPlayerRegistry() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public SpawnPointProvider getSpawnPointProvider() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public TargetFinder getTargetFinder() {
        throw new UnsupportedOperationException();
    }
}
