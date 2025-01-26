package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point for provision of segregated interfaces for a game instance.
 */
public interface GameContext {

    @NotNull
    ActionHandler getActionHandler();

    @NotNull
    AudioEmitter getAudioEmitter();

    @NotNull
    CollisionDetector getCollisionDetector();

    @NotNull
    DamageProcessor getDamageProcessor();

    @NotNull
    DeploymentInfoProvider getDeploymentInfoProvider();

    @NotNull
    EquipmentRegistry getEquipmentRegistry();

    @NotNull
    GunInfoProvider getGunInfoProvider();

    @NotNull
    GunRegistry getGunRegistry();

    @NotNull
    PlayerRegistry getPlayerRegistry();

    @NotNull
    SpawnPointProvider getSpawnPointProvider();

    @NotNull
    TargetFinder getTargetFinder();
}
