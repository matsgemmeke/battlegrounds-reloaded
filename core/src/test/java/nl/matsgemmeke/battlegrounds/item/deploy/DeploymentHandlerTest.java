package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DeploymentHandlerTest {

    private static final List<GameSound> ACTIVATION_SOUNDS = Collections.emptyList();
    private static final long ACTIVATION_DELAY = 10L;

    private ActivationProperties activationProperties;
    private AudioEmitter audioEmitter;
    private ItemEffect effect;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        activationProperties = new ActivationProperties(ACTIVATION_SOUNDS, ACTIVATION_DELAY);
        audioEmitter = mock(AudioEmitter.class);
        effect = mock(ItemEffect.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void activateDeploymentActivatesEffectAfterActivationDelay() {
        Deployer deployer = mock(Deployer.class);
        Location deployerLocation = new Location(null, 1, 1, 1);

        Entity deployerEntity = mock(Entity.class);
        when(deployerEntity.getLocation()).thenReturn(deployerLocation);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        deploymentHandler.activateDeployment(deployer, deployerEntity);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(ACTIVATION_DELAY));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, deployerLocation);
        verify(deployer).setHeldItem(null);
        verify(effect).activateInstantly();
    }

    @Test
    public void handleDeploymentDoesNotStartEffectWhenDeploymentResultIsNotSuccessful() {
        Deployer deployer = mock(Deployer.class);
        DeploymentResult result = DeploymentResult.failure();
        Entity entity = mock(Entity.class);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, entity)).thenReturn(result);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, entity);

        verifyNoInteractions(effect, taskRunner);
    }

    @Test
    public void handleDeploymentDeploysObjectIfEffectIsAlreadyPrimed() {
        Deployer deployer = mock(Deployer.class);
        Entity entity = mock(Entity.class);
        long cooldown = 10L;

        DeploymentObject object = mock(DeploymentObject.class);
        when(object.getCooldown()).thenReturn(cooldown);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, entity)).thenReturn(DeploymentResult.success(object));

        when(effect.isPrimed()).thenReturn(true);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, entity);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(cooldown));

        runnableCaptor.getValue().run();

        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
        verify(effect).deploy(object);
    }

    @Test
    public void handleDeploymentPrimesEffectIfEffectIsNotPrimed() {
        Deployer deployer = mock(Deployer.class);
        Entity entity = mock(Entity.class);
        long cooldown = 10L;

        DeploymentObject object = mock(DeploymentObject.class);
        when(object.getCooldown()).thenReturn(cooldown);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, entity)).thenReturn(DeploymentResult.success(object));

        when(effect.isPrimed()).thenReturn(false);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, entity);

        ArgumentCaptor<ItemEffectContext> effectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        verify(effect).prime(effectContextCaptor.capture());
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(cooldown));

        ItemEffectContext effectContext = effectContextCaptor.getValue();
        assertThat(effectContext.getDeployer()).isEqualTo(deployer);
        assertThat(effectContext.getEntity()).isEqualTo(entity);
        assertThat(effectContext.getSource()).isEqualTo(object);

        runnableCaptor.getValue().run();

        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseWhenNoDeploymentHasBeenPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseWhenDeploymentHasBeenPerformedWithObjectThatIsAlreadyDeployed() {
        Deployer deployer = mock(Deployer.class);
        Entity deployerEntity = mock(Entity.class);

        DeploymentObject object = mock(DeploymentObject.class);
        when(object.isDeployed()).thenReturn(true);

        DeploymentResult result = DeploymentResult.success(object);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, deployerEntity)).thenReturn(result);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    public void isAwaitingDeploymentReturnsTrueWhenDeploymentHasBeenPerformedWithObjectThatIsNotDeployed() {
        Deployer deployer = mock(Deployer.class);
        Entity deployerEntity = mock(Entity.class);

        DeploymentObject object = mock(DeploymentObject.class);
        when(object.isDeployed()).thenReturn(false);

        DeploymentResult result = DeploymentResult.success(object);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, deployerEntity)).thenReturn(result);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isTrue();
    }

    @Test
    public void isDeployedReturnsFalseWhenNoDeploymentIsPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        boolean deployed = deploymentHandler.isDeployed();

        assertThat(deployed).isFalse();
    }

    @Test
    public void isDeployedReturnsTrueWhenAnyDeploymentIsPerformed() {
        Deployer deployer = mock(Deployer.class);
        DeploymentObject object = mock(DeploymentObject.class);
        DeploymentResult result = DeploymentResult.success(object);
        Entity entity = mock(Entity.class);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, entity)).thenReturn(result);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, activationProperties, audioEmitter, effect);
        deploymentHandler.handleDeployment(deployment, deployer, entity);
        boolean deployed = deploymentHandler.isDeployed();

        assertThat(deployed).isTrue();
    }
}
