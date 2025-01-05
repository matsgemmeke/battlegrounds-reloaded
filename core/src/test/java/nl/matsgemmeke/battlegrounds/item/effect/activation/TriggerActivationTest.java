package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TriggerActivationTest {

    private ItemEffectSource source;
    private ItemHolder holder;

    @BeforeEach
    public void setUp() {
        source = mock(ItemEffectSource.class);
        holder = mock(ItemHolder.class);
    }

    @Test
    public void cancelDoesNotCancelTriggerIfNotPrimed() {
        Trigger trigger = mock(Trigger.class);

        TriggerActivation activation = new TriggerActivation();
        activation.addTrigger(trigger);
        activation.cancel();

        verify(trigger, never()).cancel();
    }

    @Test
    public void cancelCancelsTriggersIfPrimed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);
        Trigger trigger = mock(Trigger.class);

        TriggerActivation activation = new TriggerActivation();
        activation.addTrigger(trigger);
        activation.prime(context, () -> {});
        activation.cancel();

        verify(trigger).cancel();
    }

    @Test
    public void primeDoesNotPerformTriggerChecksTwiceIfAlreadyPrimed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);
        Trigger trigger = mock(Trigger.class);

        TriggerActivation activation = new TriggerActivation();
        activation.addTrigger(trigger);
        activation.prime(context, () -> {});
        activation.prime(context, () -> {});

        verify(trigger).checkTriggerActivation(context);
    }

    @Test
    public void primeRemovesHolderItemIfSourceIsDeployedAndInitiatesTriggersOnlyOnce() {
        when(source.isDeployed()).thenReturn(true);

        ItemEffectContext context = new ItemEffectContext(holder, source);
        Trigger trigger = mock(Trigger.class);

        TriggerActivation activation = new TriggerActivation();
        activation.addTrigger(trigger);
        activation.prime(context, () -> {});

        ArgumentCaptor<TriggerObserver> observerCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(observerCaptor.capture());

        verify(holder).setHeldItem(null);
        verify(trigger).checkTriggerActivation(context);
    }
}
