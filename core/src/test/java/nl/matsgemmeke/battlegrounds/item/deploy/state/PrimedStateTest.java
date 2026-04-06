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
class PrimedStateTest {

    private static final long COOLDOWN = 10L;

    @Mock
    private Deployment deployment;

    private final PrimedState state = new PrimedState();

    @Test
    @DisplayName("processAction returns same state when given result's deployment object is not physical")
    void processAction_nonPhysicalDeploymentObject() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.isPhysical()).thenReturn(false);

        DeploymentResult result = new DeploymentResult(null, deploymentObject, null, 0L);

        DeploymentState nextState = state.processAction(deployment, result);

        assertThat(nextState).isSameAs(state);

        verifyNoInteractions(deployment);
    }

    @Test
    @DisplayName("processAction returns DeployedState when given result's deployment object is physical")
    void processAction_physicalDeploymentObject() {
        Deployer deployer = mock(Deployer.class);
        Actor actor = mock(Actor.class);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.isPhysical()).thenReturn(true);

        DeploymentResult result = new DeploymentResult(deployer, deploymentObject, actor, COOLDOWN);

        DeploymentState nextState = state.processAction(deployment, result);

        assertThat(nextState).isInstanceOf(DeployedState.class);

        verify(deployment).setDeployed(true);
        verify(deployment).setPending(false);
        verify(deployment).replaceActor(actor);
        verify(deployment).scheduleDeploymentCooldown(deployer, COOLDOWN);
    }
}
