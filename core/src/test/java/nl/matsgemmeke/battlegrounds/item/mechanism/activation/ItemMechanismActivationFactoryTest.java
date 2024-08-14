package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemMechanismActivationFactoryTest {

    private Droppable item;
    private ItemMechanism mechanism;
    private Section section;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        item = mock(Droppable.class);
        mechanism = mock(ItemMechanism.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldCreateInstanceForDelayedActivationType() {
        when(section.getString("type")).thenReturn("DELAYED");

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        ItemMechanismActivation activation = factory.make(section, item, mechanism);

        assertNotNull(activation);
        assertTrue(activation instanceof DelayedActivation);
    }

    @Test
    public void shouldCreateInstanceForManualActivationType() {
        when(section.getString("type")).thenReturn("MANUAL");

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        ItemMechanismActivation activation = factory.make(section, item, mechanism);

        assertNotNull(activation);
        assertTrue(activation instanceof ManualActivation);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        factory.make(section, item, mechanism);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemMechanismActivationFactory factory = new ItemMechanismActivationFactory(taskRunner);
        factory.make(section, item, mechanism);
    }
}
