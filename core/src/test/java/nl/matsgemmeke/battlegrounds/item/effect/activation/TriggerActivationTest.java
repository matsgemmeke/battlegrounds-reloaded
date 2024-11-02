package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
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

    @Test
    public void startTriggerChecksAndActivateWhenReceivingResponse() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(true);

        TriggerActivation activation = new TriggerActivation(effect);

        Trigger trigger = mock(Trigger.class);
        doAnswer(answer -> {
            effect.activate(holder, source);
            return answer;
        }).when(trigger).checkTriggerActivation(holder, source);

        activation.addTrigger(trigger);
        activation.prime(holder, source);

        verify(trigger).checkTriggerActivation(holder, source);
        verify(effect).activate(holder, source);
        verify(holder).setHeldItem(null);
    }
}
