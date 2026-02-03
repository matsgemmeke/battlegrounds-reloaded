package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.actor.DeployerActor;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A deployment system that creates a {@link DeploymentObject}, representing the deployable item in a primed state
 * while still being held by the deployer.
 */
public class PrimeDeployment implements Deployment {

    private final AudioEmitter audioEmitter;
    private final GameEntityFinder gameEntityFinder;
    private List<GameSound> primeSounds;

    @Inject
    public PrimeDeployment(AudioEmitter audioEmitter, GameEntityFinder gameEntityFinder) {
        this.audioEmitter = audioEmitter;
        this.gameEntityFinder = gameEntityFinder;
        this.primeSounds = Collections.emptyList();
    }

    public void configurePrimeSounds(List<GameSound> primeSounds) {
        this.primeSounds = primeSounds;
    }

    @Override
    public Optional<DeploymentResult> perform(Deployer deployer, Entity deployerEntity, DestructionListener destructionListener) {
        UUID deployerUniqueId = deployer.getUniqueId();
        GameEntity gameEntity = gameEntityFinder.findGameEntityByUniqueId(deployerUniqueId).orElse(null);

        if (gameEntity == null) {
            return Optional.empty();
        }

        if (!primeSounds.isEmpty()) {
            audioEmitter.playSounds(primeSounds, deployerEntity.getLocation());
        }

        PrimeDeploymentObject deploymentObject = new PrimeDeploymentObject(deployer, deployerEntity, deployer.getHeldItem());
        DeployerActor actor = new DeployerActor(gameEntity);

        return Optional.of(new DeploymentResult(deployer, deploymentObject, actor, 0L));
    }
}
