package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ManualActivationTest {

    private Droppable item;
    private ItemHolder holder;
    private ItemMechanism mechanism;

    @Before
    public void setUp() {
        item = mock(Droppable.class);
        holder = mock(ItemHolder.class);
        mechanism = mock(ItemMechanism.class);
    }

    @Test
    public void shouldActivateMechanismWhenActivatingInstantly() {
        Item droppedItem = mock(Item.class);
        when(item.getDroppedItem()).thenReturn(droppedItem);

        ManualActivation activation = new ManualActivation(item, mechanism);
        activation.activate(holder);

        verify(mechanism).activate(droppedItem, holder);
    }

    @Test
    public void shouldSetAsPrimedWhenPriming() {
        ManualActivation activation = new ManualActivation(item, mechanism);
        activation.prime(holder);

        boolean primed = activation.isPrimed();

        assertTrue(primed);
    }
}
