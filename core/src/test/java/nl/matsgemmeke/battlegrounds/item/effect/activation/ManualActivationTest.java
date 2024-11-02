package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.junit.Before;
import org.junit.Test;

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

        verify(activator, times(2)).prepare(holder);
        verify(effect, times(2)).activate(any(ItemHolder.class), any(EffectSource.class));
        verify(effect).activate(holder, source1);
        verify(effect).activate(holder, source2);
    }
}
