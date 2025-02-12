package nl.matsgemmeke.battlegrounds.item.effect.activation;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.TriggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemEffectActivationFactoryTest {

    private DelayedActivationFactory delayedActivationFactory;
    private GameKey gameKey;
    private Section section;
    private TriggerFactory triggerFactory;

    @BeforeEach
    public void setUp() {
        delayedActivationFactory = mock(DelayedActivationFactory.class);
        gameKey = GameKey.ofSession(1);
        section = mock(Section.class);
        triggerFactory = mock(TriggerFactory.class);
    }

    @Test
    public void shouldCreateInstanceForDelayedActivationType() {
        long delayUntilActivation = 60L;

        when(section.getLong("delay-until-activation")).thenReturn(delayUntilActivation);
        when(section.getString("type")).thenReturn("DELAYED");

        ItemEffectActivation delayedActivation = mock(DelayedActivation.class);
        when(delayedActivationFactory.create(delayUntilActivation)).thenReturn(delayedActivation);

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(delayedActivationFactory, triggerFactory);
        ItemEffectActivation effectActivation = factory.create(gameKey, section, null);

        assertInstanceOf(DelayedActivation.class, effectActivation);

        verify(delayedActivationFactory).create(delayUntilActivation);
    }

    @Test
    public void shouldCreateInstanceForManualActivationType() {
        Activator activator = mock(Activator.class);

        when(section.getString("type")).thenReturn("MANUAL");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(delayedActivationFactory, triggerFactory);
        ItemEffectActivation effectActivation = factory.create(gameKey, section, activator);

        assertInstanceOf(ManualActivation.class, effectActivation);
    }

    @Test
    public void throwExceptionWhenCreatingManualActivationWithoutActivator() {
        when(section.getString("type")).thenReturn("MANUAL");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(delayedActivationFactory, triggerFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(gameKey, section, null));
    }

    @Test
    public void shouldCreateInstanceForTriggerActivationType() {
        Map<String, Object> triggerConfig = Map.of(
                "type", "FLOOR_HIT",
                "period-between-checks", 10
        );
        List<Map<String, Object>> triggers = List.of(triggerConfig);

        when(section.get("triggers")).thenReturn(triggers);
        when(section.getString("type")).thenReturn("TRIGGER");

        Trigger trigger = mock(Trigger.class);
        when(triggerFactory.create(gameKey, triggerConfig)).thenReturn(trigger);

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(delayedActivationFactory, triggerFactory);
        ItemEffectActivation effectActivation = factory.create(gameKey, section, null);

        assertInstanceOf(TriggerActivation.class, effectActivation);
    }

    @Test
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(delayedActivationFactory, triggerFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(gameKey, section, null));
    }

    @Test
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemEffectActivationFactory factory = new ItemEffectActivationFactory(delayedActivationFactory, triggerFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(gameKey, section, null));
    }
}
