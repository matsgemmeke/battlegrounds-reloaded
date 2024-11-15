package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TriggerActivationTest {

    private ItemEffect effect;
    private ItemHolder holder;

    @BeforeEach
    public void setUp() {
        effect = mock(ItemEffect.class);
        holder = mock(ItemHolder.class);
    }

    @Test
    public void startTriggerChecksAndActivateWhenReceivingResponse() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(true);

        Trigger trigger = mock(Trigger.class);
        doAnswer(answer -> {
            effect.activate(answer.getArgument(0));
            return answer;
        }).when(trigger).checkTriggerActivation(any(ItemEffectContext.class));

        TriggerActivation activation = new TriggerActivation(effect);
        activation.addTrigger(trigger);
        activation.prime(holder, source);

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(trigger).checkTriggerActivation(contextCaptor.capture());

        verify(effect).activate(contextCaptor.getValue());
        verify(holder).setHeldItem(null);
    }
}
