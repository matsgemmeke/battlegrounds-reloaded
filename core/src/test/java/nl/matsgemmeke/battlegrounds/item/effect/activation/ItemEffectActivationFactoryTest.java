package nl.matsgemmeke.battlegrounds.item.effect.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemEffectActivationFactoryTest {

    private GameContext context;
    private ItemEffect effect;
    private Section section;
    private TaskRunner taskRunner;

    @BeforeEach
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
        ItemEffectActivation activation = factory.make(context, effect, section, null);

        assertInstanceOf(DelayedActivation.class, activation);
    }

    @Test
    public void shouldCreateInstanceForManualActivationType() {
        Activator activator = mock(Activator.class);

        when(section.getString("type")).thenReturn("MANUAL");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);
        ItemEffectActivation activation = factory.make(context, effect, section, activator);

        assertInstanceOf(ManualActivation.class, activation);
    }

    @Test
    public void throwExceptionWhenCreatingManualActivationWithoutActivator() {
        when(section.getString("type")).thenReturn("MANUAL");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.make(context, effect, section, null));
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
        ItemEffectActivation activation = factory.make(context, effect, section, null);

        assertInstanceOf(TriggerActivation.class, activation);
    }

    @Test
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.make(context, effect, section, null));
    }

    @Test
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(taskRunner);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.make(context, effect, section, null));
    }
}
