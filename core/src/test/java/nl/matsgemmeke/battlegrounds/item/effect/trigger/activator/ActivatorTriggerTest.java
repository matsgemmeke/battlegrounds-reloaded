package nl.matsgemmeke.battlegrounds.item.effect.trigger.activator;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class ActivatorTriggerTest {

    private Activator activator;
    private Deployer deployer;
    private Entity entity;
    private ItemEffectContext context;
    private ItemEffectSource source;

    @BeforeEach
    public void setUp() {
        activator = mock(Activator.class);
        deployer = mock(Deployer.class);
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        context = new ItemEffectContext(deployer, entity, source);
    }

    @Test
    public void isPrimedReturnsFalseWhenTriggerIsNotPrimed() {
        ActivatorTrigger trigger = new ActivatorTrigger(activator);
        boolean primed = trigger.isPrimed();

        assertThat(primed).isFalse();
    }

    @Test
    public void isPrimedReturnsTrueWhenTriggerIsPrimed() {
        Deployer deployer = mock(Deployer.class);
        Entity entity = mock(Entity.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        ActivatorTrigger trigger = new ActivatorTrigger(activator);
        trigger.prime(context);
        boolean primed = trigger.isPrimed();

        assertThat(primed).isTrue();
    }

    @Test
    public void cancelDoesNotRemoveActivatorIfNotPrimed() {
        ActivatorTrigger trigger = new ActivatorTrigger(activator);
        trigger.cancel();

        verify(activator, never()).remove();
    }

    @Test
    public void cancelRemovesActivatorIfPrimed() {
        ActivatorTrigger trigger = new ActivatorTrigger(activator);
        trigger.prime(context);
        trigger.cancel();

        verify(activator).remove();
    }

    @Test
    public void primeDoesNotPrepareActivatorAgainIfAlreadyPrimed() {
        ActivatorTrigger trigger = new ActivatorTrigger(activator);
        trigger.prime(context);
        trigger.prime(context);

        verify(activator, times(1)).prepare(deployer);
    }

    @Test
    public void primePreparesActivatorIfNotPrimed() {
        ActivatorTrigger trigger = new ActivatorTrigger(activator);
        trigger.prime(context);

        verify(activator).prepare(deployer);
    }
}
