package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ScheduledTriggerTest {

    @Test
    void activatesAlwaysReturnsTrue() {
        TriggerContext context = new TriggerContext(UUID.randomUUID(), mock(TriggerTarget.class));

        ScheduledTrigger trigger = new ScheduledTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isTrue();
    }
}
