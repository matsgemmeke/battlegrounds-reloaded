package nl.matsgemmeke.battlegrounds.item.equipment.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.equipment.mechanism.EquipmentMechanism;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EquipmentActivationFactoryTest {

    private EquipmentMechanism mechanism;
    private Section section;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        mechanism = mock(EquipmentMechanism.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldCreateInstanceForDelayedActivationType() {
        when(section.getString("type")).thenReturn("DELAYED_TRIGGER");

        EquipmentActivationFactory factory = new EquipmentActivationFactory(taskRunner);
        EquipmentActivation activation = factory.make(section, mechanism);

        assertNotNull(activation);
        assertTrue(activation instanceof DelayedActivation);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        EquipmentActivationFactory factory = new EquipmentActivationFactory(taskRunner);
        factory.make(section, mechanism);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        EquipmentActivationFactory factory = new EquipmentActivationFactory(taskRunner);
        factory.make(section, mechanism);
    }
}
