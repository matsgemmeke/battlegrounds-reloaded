package nl.matsgemmeke.battlegrounds.item.deploy.state;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdleStateTest {

    private static final long COOLDOWN = 10L;

    @Mock
    private Deployment deployment;

    private final IdleState state = new IdleState();

    @Test
    @DisplayName("processAction starts triggers and returns DeployedState when deployment object is physical")
    void processAction_physicalDeploymentObject() {
        Deployer deployer = mock(Deployer.class);
        Actor actor = mock(Actor.class);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.isPhysical()).thenReturn(true);

        DeploymentResult result = new DeploymentResult(deployer, deploymentObject, actor, COOLDOWN);

        DeploymentState nextState = state.processAction(deployment, result);

        assertThat(nextState).isInstanceOf(DeploymentState.class);

        verify(deployment).setDeployed(true);
        verify(deployment).startTriggerExecutors(deployer, actor);
        verify(deployment).scheduleDeploymentCooldown(deployer, COOLDOWN);
    }

    @Test
    @DisplayName("processAction returns PrimedState when deployment object is not physical")
    void processAction_nonPhysicalDeploymentObject() {
        Deployer deployer = mock(Deployer.class);
        Actor actor = mock(Actor.class);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.isPhysical()).thenReturn(false);

        DeploymentResult result = new DeploymentResult(deployer, deploymentObject, actor, COOLDOWN);

        DeploymentState nextState = state.processAction(deployment, result);

        assertThat(nextState).isInstanceOf(PrimedState.class);

        verify(deployment).setPending(true);
        verify(deployment).startTriggerExecutors(deployer, actor);
    }
}
