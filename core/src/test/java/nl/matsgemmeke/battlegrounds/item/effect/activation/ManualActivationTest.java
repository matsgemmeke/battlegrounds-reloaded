package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ManualActivationTest {

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
    public void cancelDoesNotRemoveActivatorIfNotPrimed() {
        ManualActivation activation = new ManualActivation(activator);
        activation.cancel();

        verify(activator, never()).remove();
    }

    @Test
    public void cancelRemovesActivatorIfPrimed() {
        ManualActivation activation = new ManualActivation(activator);
        activation.prime(context, () -> {});
        activation.cancel();

        verify(activator).remove();
    }

    @Test
    public void primeDoesNotPrepareActivatorAgainIfAlreadyPrimed() {
        ManualActivation activation = new ManualActivation(activator);
        activation.prime(context, () -> {});
        activation.prime(context, () -> {});

        verify(activator).prepare(deployer);
    }

    @Test
    public void primePreparesActivatorIfNotPrimed() {
        ManualActivation activation = new ManualActivation(activator);
        activation.prime(context, () -> {});

        verify(activator).prepare(deployer);
    }
}
