package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemMechanismActivationFactoryTest {

    private GameContext context;
    private ItemMechanism mechanism;
    private Section section;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        context = mock(GameContext.class);
        mechanism = mock(ItemMechanism.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldCreateInstanceForDelayedActivationType() {
        when(section.getString("type")).thenReturn("DELAYED");

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        ItemMechanismActivation activation = factory.make(context, mechanism, section);

        assertTrue(activation instanceof DelayedActivation);
    }

    @Test
    public void shouldCreateInstanceForManualActivationType() {
        when(section.getString("type")).thenReturn("MANUAL");

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        ItemMechanismActivation activation = factory.make(context, mechanism, section);

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

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        ItemMechanismActivation activation = factory.make(context, mechanism, section);

        assertTrue(activation instanceof TriggerActivation);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        factory.make(context, mechanism, section);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        factory.make(context, mechanism, section);
    }
}
