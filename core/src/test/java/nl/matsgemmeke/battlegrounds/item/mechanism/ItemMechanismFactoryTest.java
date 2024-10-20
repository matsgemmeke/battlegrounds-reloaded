package nl.matsgemmeke.battlegrounds.item.mechanism;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.mechanism.flash.FlashMechanism;
import nl.matsgemmeke.battlegrounds.util.MetadataValueCreator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemMechanismFactoryTest {

    private GameContext context;
    private MetadataValueCreator metadataValueCreator;
    private Section section;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        context = mock(GameContext.class);
        metadataValueCreator = mock(MetadataValueCreator.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldCreateInstanceForCombustionMechanismType() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(context.getAudioEmitter()).thenReturn(audioEmitter);

        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("COMBUSTION");

        ItemMechanismFactory factory = new ItemMechanismFactory(metadataValueCreator, taskRunner);
        ItemMechanism mechanism = factory.make(section, context);

        assertTrue(mechanism instanceof CombustionMechanism);
    }

    @Test
    public void shouldCreateInstanceForExplosionMechanismType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("EXPLOSION");

        ItemMechanismFactory factory = new ItemMechanismFactory(metadataValueCreator, taskRunner);
        ItemMechanism mechanism = factory.make(section, context);

        assertTrue(mechanism instanceof ExplosionMechanism);
    }

    @Test
    public void shouldCreateInstanceForFlashMechanismType() {
        TargetFinder targetFinder = mock(TargetFinder.class);
        when(context.getTargetFinder()).thenReturn(targetFinder);

        when(section.getString("type")).thenReturn("FLASH");

        ItemMechanismFactory factory = new ItemMechanismFactory(metadataValueCreator, taskRunner);
        ItemMechanism mechanism = factory.make(section, context);

        assertTrue(mechanism instanceof FlashMechanism);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemMechanismFactory factory = new ItemMechanismFactory(metadataValueCreator, taskRunner);
        factory.make(section, context);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemMechanismFactory factory = new ItemMechanismFactory(metadataValueCreator, taskRunner);
        factory.make(section, context);
    }
}
