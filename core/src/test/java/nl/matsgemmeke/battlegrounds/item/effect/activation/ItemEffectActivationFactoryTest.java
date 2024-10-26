package nl.matsgemmeke.battlegrounds.item.effect.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemEffectActivationFactoryTest {

    private GameContext context;
    private ItemEffect effect;
    private Section section;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        context = mock(GameContext.class);
        effect = mock(ItemEffect.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldCreateInstanceForDelayedActivationType() {
        when(section.getString("type")).thenReturn("DELAYED");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);
        ItemEffectActivation activation = factory.make(context, effect, section);

        assertTrue(activation instanceof DelayedActivation);
    }

    @Test
    public void shouldCreateInstanceForManualActivationType() {
        when(section.getString("type")).thenReturn("MANUAL");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);
        ItemEffectActivation activation = factory.make(context, effect, section);

        assertTrue(activation instanceof ManualActivation);
    }

    @Test
    public void shouldCreateInstanceForTriggerActivationType() {
        Map<String, Object> trigger = Map.of(
                "type", "FLOOR_HIT",
                "period-between-checks", 10
        );
        List<Map<String, Object>> triggers = List.of(trigger);

        when(section.get("triggers")).thenReturn(triggers);
        when(section.getString("type")).thenReturn("TRIGGER");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);
        ItemEffectActivation activation = factory.make(context, effect, section);

        assertTrue(activation instanceof TriggerActivation);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);
        factory.make(context, effect, section);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);
        factory.make(context, effect, section);
    }
}
