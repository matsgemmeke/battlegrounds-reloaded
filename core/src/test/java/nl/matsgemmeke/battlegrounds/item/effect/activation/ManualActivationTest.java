package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ManualActivationTest {

    private Activator activator;
    private ItemEffect effect;
    private ItemHolder holder;

    @Before
    public void setUp() {
        activator = mock(Activator.class);
        effect = mock(ItemEffect.class);
        holder = mock(ItemHolder.class);
    }

    @Test
    public void shouldActivateEffectAtAllSourcesWhenActivating() {
        EffectSource source1 = mock(EffectSource.class);
        EffectSource source2 = mock(EffectSource.class);

        ManualActivation activation = new ManualActivation(effect, activator);
        activation.prime(holder, source1);
        activation.prime(holder, source2);
        activation.activateInstantly(holder);

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect, times(2)).activate(contextCaptor.capture());

        assertEquals(source1, contextCaptor.getAllValues().get(0).getSource());
        assertEquals(source2, contextCaptor.getAllValues().get(1).getSource());

        verify(activator, times(2)).prepare(holder);
    }
}
