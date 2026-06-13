package nl.matsgemmeke.battlegrounds.item.deploy.action;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.actor.HeldItemActor;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.deploy.object.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.object.HeldDeploymentObject;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A deployment system that creates a {@link DeploymentObject}, representing the deployable item in a primed state
 * while still being held by the deployer.
 */
public class PrimeDeploymentAction implements DeploymentAction {

    private final AudioEmitter audioEmitter;
    private final GameEntityFinder gameEntityFinder;
    private List<GameSound> primeSounds;

    @Inject
    public PrimeDeploymentAction(AudioEmitter audioEmitter, GameEntityFinder gameEntityFinder) {
        this.audioEmitter = audioEmitter;
        this.gameEntityFinder = gameEntityFinder;
        this.primeSounds = Collections.emptyList();
    }

    public void configurePrimeSounds(List<GameSound> primeSounds) {
        this.primeSounds = primeSounds;
    }

    @Override
    public Optional<DeploymentResult> perform(DeploymentContext context) {
        Deployer deployer = context.deployer();
        UUID deployerUniqueId = deployer.getUniqueId();
        GameEntity gameEntity = gameEntityFinder.findGameEntityByUniqueId(deployerUniqueId).orElse(null);

        if (gameEntity == null) {
            return Optional.empty();
        }

        if (!primeSounds.isEmpty()) {
            audioEmitter.playSounds(primeSounds, deployer.getDeployLocation());
        }

        ItemStack heldItemStack = deployer.getHeldItem();
        HeldDeploymentObject deploymentObject = new HeldDeploymentObject(deployer, deployer.getHeldItem());
        HeldItemActor actor = new HeldItemActor(deployer, gameEntity, heldItemStack);

        return Optional.of(new DeploymentResult(deployer, deploymentObject, actor, 0L));
    }
}
