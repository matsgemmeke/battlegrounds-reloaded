package nl.matsgemmeke.battlegrounds.item.deploy.action;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.actor.HeldItemActor;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.deploy.object.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.object.HeldDeploymentObject;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * A deployment system that creates a {@link DeploymentObject}, representing the deployable item in a primed state
 * while still being held by the deployer.
 */
public class PrimeDeploymentAction implements DeploymentAction {

    private final GameEntityFinder gameEntityFinder;
    private final SoundEmitter soundEmitter;
    @Nullable
    private SoundSequence soundSequence;

    @Inject
    public PrimeDeploymentAction(GameEntityFinder gameEntityFinder, SoundEmitter soundEmitter) {
        this.gameEntityFinder = gameEntityFinder;
        this.soundEmitter = soundEmitter;
    }

    public void configureSoundSequence(SoundSequence soundSequence) {
        this.soundSequence = soundSequence;
    }

    @Override
    public Optional<DeploymentResult> perform(DeploymentContext context) {
        Deployer deployer = context.deployer();
        UUID deployerUniqueId = deployer.getUniqueId();
        GameEntity gameEntity = gameEntityFinder.findGameEntityByUniqueId(deployerUniqueId).orElse(null);

        if (gameEntity == null) {
            return Optional.empty();
        }

        if (soundSequence != null) {
            soundEmitter.playSoundSequence(soundSequence, deployer::getDeployLocation);
        }

        ItemStack heldItemStack = deployer.getHeldItem();
        HeldDeploymentObject deploymentObject = new HeldDeploymentObject(deployer, deployer.getHeldItem());
        HeldItemActor actor = new HeldItemActor(deployer, gameEntity, heldItemStack);

        return Optional.of(new DeploymentResult(deployer, deploymentObject, actor, 0L));
    }
}
