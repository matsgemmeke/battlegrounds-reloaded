package nl.matsgemmeke.battlegrounds.item.deploy.state;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DeployedStateTest {

    @Mock
    private Deployment deployment;

    private final DeployedState state = new DeployedState();

    @Test
    @DisplayName("processAction returns same state instance")
    void processAction() {
        DeploymentResult result = new DeploymentResult(null, null, null, 0L);

        DeploymentState nextState = state.processAction(deployment, result);

        assertThat(nextState).isSameAs(state);
    }
}
