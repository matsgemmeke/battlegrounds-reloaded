package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A deployment system that creates a {@link DeploymentObject}, representing the deployable item in a primed state
 * while still being held by the deployer.
 */
public class PrimeDeployment implements Deployment {

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final List<GameSound> primeSounds;

    public PrimeDeployment(@NotNull AudioEmitter audioEmitter, @NotNull List<GameSound> primeSounds) {
        this.audioEmitter = audioEmitter;
        this.primeSounds = primeSounds;
    }

    @NotNull
    public DeploymentObject perform(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        audioEmitter.playSounds(primeSounds, deployerEntity.getLocation());

        return new HeldItem(deployer, deployerEntity, deployer.getHeldItem());
    }
}
