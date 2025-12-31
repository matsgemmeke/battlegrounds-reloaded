package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.DeployerTriggerTarget;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A deployment system that creates a {@link DeploymentObject}, representing the deployable item in a primed state
 * while still being held by the deployer.
 */
public class PrimeDeployment implements Deployment {

    private final AudioEmitter audioEmitter;
    private List<GameSound> primeSounds;

    @Inject
    public PrimeDeployment(AudioEmitter audioEmitter) {
        this.audioEmitter = audioEmitter;
        this.primeSounds = Collections.emptyList();
    }

    public void configurePrimeSounds(List<GameSound> primeSounds) {
        this.primeSounds = primeSounds;
    }

    @Override
    public Optional<DeploymentResult> perform(Deployer deployer, Entity deployerEntity, DestructionListener destructionListener) {
        if (!primeSounds.isEmpty()) {
            audioEmitter.playSounds(primeSounds, deployerEntity.getLocation());
        }

        PrimeDeploymentObject deploymentObject = new PrimeDeploymentObject(deployer, deployerEntity, deployer.getHeldItem());
        DeployerTriggerTarget triggerTarget = new DeployerTriggerTarget();

        return Optional.of(new DeploymentResult(deployer, deploymentObject, triggerTarget, 0L));
    }
}
