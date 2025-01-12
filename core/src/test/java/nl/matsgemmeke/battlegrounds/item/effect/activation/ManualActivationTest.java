package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ManualActivationTest {

    private Activator activator;
    private ItemEffectSource source;
    private ItemHolder holder;

    @BeforeEach
    public void setUp() {
        activator = mock(Activator.class);
        source = mock(ItemEffectSource.class);
        holder = mock(ItemHolder.class);
    }

    @Test
    public void cancelDoesNotRemoveActivatorIfNotPrimed() {
        ManualActivation activation = new ManualActivation(activator);
        activation.cancel();

        verify(activator, never()).remove();
    }

    @Test
    public void cancelRemovesActivatorIfPrimed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);

        ManualActivation activation = new ManualActivation(activator);
        activation.prime(context, () -> {});
        activation.cancel();

        verify(activator).remove();
    }

    @Test
    public void primeDoesNotPrepareActivatorAgainIfAlreadyPrimed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);

        ManualActivation activation = new ManualActivation(activator);
        activation.prime(context, () -> {});
        activation.prime(context, () -> {});

        verify(activator).prepare(holder);
    }

    @Test
    public void primePreparesActivatorIfNotPrimed() {
        ItemEffectContext context = new ItemEffectContext(holder, source);

        ManualActivation activation = new ManualActivation(activator);
        activation.prime(context, () -> {});

        verify(activator).prepare(holder);
    }
}
