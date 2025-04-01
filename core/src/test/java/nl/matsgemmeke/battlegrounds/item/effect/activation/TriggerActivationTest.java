package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.TriggerObserver;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TriggerActivationTest {

    private Deployer deployer;
    private Entity entity;
    private ItemEffectContext context;
    private ItemEffectSource source;

    @BeforeEach
    public void setUp() {
        deployer = mock(Deployer.class);
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        context = new ItemEffectContext(deployer, entity, source);
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
        Trigger trigger = mock(Trigger.class);

        TriggerActivation activation = new TriggerActivation();
        activation.addTrigger(trigger);
        activation.prime(context, () -> {});
        activation.cancel();

        verify(trigger).cancel();
    }

    @Test
    public void primeDoesNotPerformTriggerChecksTwiceIfAlreadyPrimed() {
        Trigger trigger = mock(Trigger.class);

        TriggerActivation activation = new TriggerActivation();
        activation.addTrigger(trigger);
        activation.prime(context, () -> {});
        activation.prime(context, () -> {});

        verify(trigger).checkTriggerActivation(context);
    }

    @Test
    public void primeRemovesItemFromDeployerIfSourceIsDeployedAndInitiatesTriggersOnlyOnce() {
        Trigger trigger = mock(Trigger.class);

        when(source.isDeployed()).thenReturn(true);

        TriggerActivation activation = new TriggerActivation();
        activation.addTrigger(trigger);
        activation.prime(context, () -> {});

        ArgumentCaptor<TriggerObserver> observerCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(observerCaptor.capture());

        verify(deployer).setHeldItem(null);
        verify(trigger).checkTriggerActivation(context);
    }
}
