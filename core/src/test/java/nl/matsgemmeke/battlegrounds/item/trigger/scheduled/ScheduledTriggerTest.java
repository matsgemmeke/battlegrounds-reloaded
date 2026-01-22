package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ScheduledTriggerTest {

    @Test
    void checkAlwaysReturnTriggerResultThatActivates() {
        TriggerContext context = new TriggerContext(UUID.randomUUID(), mock(TriggerTarget.class));

        ScheduledTrigger trigger = new ScheduledTrigger();
        TriggerResult triggerResult = trigger.check(context);

        assertThat(triggerResult.activates()).isTrue();
    }
}
