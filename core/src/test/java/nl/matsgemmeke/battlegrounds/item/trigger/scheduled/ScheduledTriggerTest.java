package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ScheduledTriggerTest {

    @Test
    public void activatesAlwaysReturnsTrue() {
        TriggerContext context = new TriggerContext(mock(Entity.class), mock(TriggerTarget.class));

        ScheduledTrigger trigger = new ScheduledTrigger();
        boolean activates = trigger.activates(context);

        assertThat(activates).isTrue();
    }
}
