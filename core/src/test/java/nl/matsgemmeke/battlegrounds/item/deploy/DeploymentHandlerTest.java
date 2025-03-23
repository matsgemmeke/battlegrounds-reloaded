package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DeploymentHandlerTest {

    private ItemEffect effect;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        effect = mock(ItemEffect.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void isPerformingReturnsFalseWhenNotHandlingDeployment() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, effect);
        boolean performing = deploymentHandler.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    public void isPerformingReturnsFalseWhenHandlingDeployment() {
        Deployer deployer = mock(Deployer.class);
        DeploymentObject object = mock(DeploymentObject.class);
        DeploymentResult result = DeploymentResult.success(object);
        Entity entity = mock(Entity.class);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, entity)).thenReturn(result);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, effect);
        deploymentHandler.handleDeployment(deployment, deployer, entity);
        boolean performing = deploymentHandler.isPerforming();

        assertThat(performing).isTrue();
    }

    @Test
    public void handleDeploymentDoesNotStartEffectWhenDeploymentResultIsNotSuccessful() {
        Deployer deployer = mock(Deployer.class);
        DeploymentResult result = DeploymentResult.failure();
        Entity entity = mock(Entity.class);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, entity)).thenReturn(result);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, effect);
        deploymentHandler.handleDeployment(deployment, deployer, entity);

        verifyNoInteractions(effect, taskRunner);
    }

    @Test
    public void handleDeploymentDeploysObjectIfEffectIsAlreadyPrimed() {
        Deployer deployer = mock(Deployer.class);
        DeploymentObject object = mock(DeploymentObject.class);
        DeploymentResult result = DeploymentResult.success(object);
        Entity entity = mock(Entity.class);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, entity)).thenReturn(result);

        when(effect.isPrimed()).thenReturn(true);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, effect);
        deploymentHandler.handleDeployment(deployment, deployer, entity);

        verify(effect).deploy(object);
    }

    @Test
    public void handleDeploymentPrimesEffectIfEffectIsNotPrimed() {
        Deployer deployer = mock(Deployer.class);
        DeploymentObject object = mock(DeploymentObject.class);
        DeploymentResult result = DeploymentResult.success(object);
        Entity entity = mock(Entity.class);

        Deployment deployment = mock(Deployment.class);
        when(deployment.perform(deployer, entity)).thenReturn(result);

        when(effect.isPrimed()).thenReturn(false);

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, effect);
        deploymentHandler.handleDeployment(deployment, deployer, entity);

        ArgumentCaptor<ItemEffectContext> effectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).prime(effectContextCaptor.capture());

        ItemEffectContext effectContext = effectContextCaptor.getValue();
        assertThat(effectContext.getDeployer()).isEqualTo(deployer);
        assertThat(effectContext.getEntity()).isEqualTo(entity);
        assertThat(effectContext.getSource()).isEqualTo(object);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseWhenNoDeploymentHasBeenPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, effect);
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

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, effect);
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

        DeploymentHandler deploymentHandler = new DeploymentHandler(taskRunner, effect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isTrue();
    }
}
