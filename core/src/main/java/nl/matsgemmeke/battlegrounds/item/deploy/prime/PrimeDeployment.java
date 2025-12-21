package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
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
    public Optional<DeploymentContext> createContext(Deployer deployer, Entity deployerEntity) {
        if (!primeSounds.isEmpty()) {
            audioEmitter.playSounds(primeSounds, deployerEntity.getLocation());
        }

        PrimeDeploymentObject object = new PrimeDeploymentObject(deployer, deployerEntity, deployer.getHeldItem());

        return Optional.of(new DeploymentContext(deployerEntity, object, deployer, null, 0L));
    }
}
