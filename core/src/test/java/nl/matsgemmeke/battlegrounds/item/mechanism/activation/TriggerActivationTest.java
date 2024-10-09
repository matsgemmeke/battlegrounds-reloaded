package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger.Trigger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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

    @Test(expected = UnsupportedOperationException.class)
    public void throwErrorWhenAttemptingToStartWithDeferredObject() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        TriggerActivation activation = new TriggerActivation(mechanism);
        activation.primeInHand(holder, itemStack);
    }

    @Test
    public void startTriggerChecksAndActivateWhenReceivingResponse() {
        Deployable object = mock(Deployable.class);

        TriggerActivation activation = new TriggerActivation(mechanism);

        Trigger trigger = mock(Trigger.class);
        doAnswer(answer -> {
            mechanism.activate(holder, object);
            return answer;
        }).when(trigger).checkTriggerActivation(holder, object);

        activation.addTrigger(trigger);
        activation.primeDeployedObject(holder, object);

        verify(trigger).checkTriggerActivation(holder, object);
        verify(mechanism).activate(holder, object);
    }
}
