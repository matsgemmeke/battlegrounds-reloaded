package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger.Trigger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class TriggerActivationTest {

    private ItemHolder holder;
    private ItemMechanism mechanism;

    @Before
    public void setUp() {
        holder = mock(ItemHolder.class);
        mechanism = mock(ItemMechanism.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwErrorWhenAttemptingToStartWithDeferredObject() {
        TriggerActivation activation = new TriggerActivation(mechanism);
        activation.prime(holder, null);
    }

    @Test
    public void startTriggerChecksAndActivateWhenReceivingResponse() {
        Deployable object = mock(Deployable.class);

        TriggerActivation activation = new TriggerActivation(mechanism);

        Trigger trigger = mock(Trigger.class);
        doAnswer(answer -> {
            activation.handleActivation(holder, object);
            return answer;
        }).when(trigger).checkTriggerActivation(holder, object);

        activation.addTrigger(trigger);
        activation.prime(holder, object);

        verify(trigger).checkTriggerActivation(holder, object);
        verify(mechanism).activate(holder, object);
    }
}
