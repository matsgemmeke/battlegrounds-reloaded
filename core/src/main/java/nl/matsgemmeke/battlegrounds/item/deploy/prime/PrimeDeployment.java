package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * A deployment system that creates a {@link DeploymentObject}, representing the deployable item in a primed state
 * while still being held by the deployer.
 */
public class PrimeDeployment implements Deployment {

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private List<GameSound> primeSounds;

    @Inject
    public PrimeDeployment(@NotNull AudioEmitter audioEmitter) {
        this.audioEmitter = audioEmitter;
        this.primeSounds = Collections.emptyList();
    }

    public void configurePrimeSounds(List<GameSound> primeSounds) {
        this.primeSounds = primeSounds;
    }

    @NotNull
    public DeploymentResult perform(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        if (!primeSounds.isEmpty()) {
            audioEmitter.playSounds(primeSounds, deployerEntity.getLocation());
        }

        PrimeDeploymentObject object = new PrimeDeploymentObject(deployer, deployerEntity, deployer.getHeldItem());

        return DeploymentResult.success(object);
    }
}
