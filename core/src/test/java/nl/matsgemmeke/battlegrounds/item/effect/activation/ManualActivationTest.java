package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ManualActivationTest {

    private Activator activator;
    private EffectSource source;
    private ItemHolder holder;

    @BeforeEach
    public void setUp() {
        activator = mock(Activator.class);
        source = mock(EffectSource.class);
        holder = mock(ItemHolder.class);
    }

    @Test
    public void activatePreparesActivatorAndFinishesActivationRightAway() {
        ItemEffectContext context = new ItemEffectContext(holder, source);
        Procedure onActivate = mock(Procedure.class);

        ManualActivation activation = new ManualActivation(activator);
        activation.prime(context, onActivate);

        verify(activator).prepare(holder);
    }
}
