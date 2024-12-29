package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TriggerActivationTest {

    private ItemHolder holder;

    @BeforeEach
    public void setUp() {
        holder = mock(ItemHolder.class);
    }

    @Test
    public void activateRemovesHolderItemIfSourceIsDeployedAndInitiatesTrigger() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(true);

        ItemEffectContext context = new ItemEffectContext(holder, source);
        Procedure onActivate = mock(Procedure.class);

        Trigger trigger = mock(Trigger.class);

        TriggerActivation activation = new TriggerActivation();
        activation.addTrigger(trigger);
        activation.prime(context, onActivate);

        ArgumentCaptor<TriggerObserver> observerCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(observerCaptor.capture());

        verify(holder).setHeldItem(null);
        verify(trigger).checkTriggerActivation(context);
    }
}
