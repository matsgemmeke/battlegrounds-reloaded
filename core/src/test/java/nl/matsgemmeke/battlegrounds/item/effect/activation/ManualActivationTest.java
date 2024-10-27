package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class ManualActivationTest {

    private ItemEffect effect;
    private ItemHolder holder;

    @Before
    public void setUp() {
        effect = mock(ItemEffect.class);
        holder = mock(ItemHolder.class);
    }

    @Test
    public void shouldActivateEffectForAllDeployedObjectsWhenActivating() {
        Deployable object1 = mock(Deployable.class);
        Deployable object2 = mock(Deployable.class);

        ManualActivation activation = new ManualActivation(effect);
        activation.primeDeployedObject(holder, object1);
        activation.primeDeployedObject(holder, object2);
        activation.activateDeployedObjects(holder);

        verify(effect, times(2)).activate(eq(holder), any(Deployable.class));
    }

    @Test
    public void isPrimedReturnsFalseWhenMostRecentDeployedObjectIsNotNull() {
        Deployable object = mock(Deployable.class);

        ManualActivation activation = new ManualActivation(effect);
        activation.primeDeployedObject(holder, object);

        boolean primed = activation.isPrimed();

        assertFalse(primed);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void throwErrorWhenDeployingDeferredObject() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ManualActivation activation = new ManualActivation(effect);
        activation.primeInHand(holder, itemStack);
    }
}