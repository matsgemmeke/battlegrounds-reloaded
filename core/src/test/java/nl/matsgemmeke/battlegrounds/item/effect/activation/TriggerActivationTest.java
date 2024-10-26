package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class TriggerActivationTest {

    private ItemEffect effect;
    private ItemHolder holder;

    @Before
    public void setUp() {
        effect = mock(ItemEffect.class);
        holder = mock(ItemHolder.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void throwErrorWhenAttemptingToStartWithDeferredObject() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        TriggerActivation activation = new TriggerActivation(effect);
        activation.primeInHand(holder, itemStack);
    }

    @Test
    public void startTriggerChecksAndActivateWhenReceivingResponse() {
        Deployable object = mock(Deployable.class);

        TriggerActivation activation = new TriggerActivation(effect);

        Trigger trigger = mock(Trigger.class);
        doAnswer(answer -> {
            effect.activate(holder, object);
            return answer;
        }).when(trigger).checkTriggerActivation(holder, object);

        activation.addTrigger(trigger);
        activation.primeDeployedObject(holder, object);

        verify(trigger).checkTriggerActivation(holder, object);
        verify(effect).activate(holder, object);
    }
}
