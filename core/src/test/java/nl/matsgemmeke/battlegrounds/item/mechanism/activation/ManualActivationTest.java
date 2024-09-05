package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class ManualActivationTest {

    private ItemHolder holder;
    private ItemMechanism mechanism;

    @Before
    public void setUp() {
        holder = mock(ItemHolder.class);
        mechanism = mock(ItemMechanism.class);
    }

    @Test
    public void shouldActivateMechanismForAllDeployedObjectsWhenActivating() {
        Deployable object1 = mock(Deployable.class);
        Deployable object2 = mock(Deployable.class);

        ManualActivation activation = new ManualActivation(mechanism);
        activation.prime(holder, object1);
        activation.prime(holder, object2);
        activation.activate(holder);

        verify(mechanism, times(2)).activate(eq(holder), any(Deployable.class));
    }

    @Test
    public void isPrimingReturnsFalseWhenMostRecentDeployedObjectIsNotNull() {
        Deployable object = mock(Deployable.class);

        ManualActivation activation = new ManualActivation(mechanism);
        activation.prime(holder, object);

        boolean priming = activation.isPriming();

        assertFalse(priming);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwErrorWhenDeployingDeferredObject() {
        ManualActivation activation = new ManualActivation(mechanism);
        activation.prime(holder, null);
    }
}
