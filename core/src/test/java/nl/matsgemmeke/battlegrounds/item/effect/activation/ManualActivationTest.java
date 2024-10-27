package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
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
    public void shouldActivateEffectAtAllSourcesWhenActivating() {
        EffectSource source1 = mock(EffectSource.class);
        EffectSource source2 = mock(EffectSource.class);

        ManualActivation activation = new ManualActivation(effect);
        activation.prime(holder, source1);
        activation.prime(holder, source2);
        activation.activateInstantly(holder);

        verify(effect, times(2)).activate(any(ItemHolder.class), any(EffectSource.class));
        verify(effect).activate(holder, source1);
        verify(effect).activate(holder, source2);
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
