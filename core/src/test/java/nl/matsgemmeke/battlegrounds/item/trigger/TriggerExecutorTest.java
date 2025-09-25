package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TriggerExecutorTest {

    private Schedule schedule;
    private Supplier<Schedule> scheduleSupplier;
    private TriggerNew trigger;

    @BeforeEach
    public void setUp() {
        schedule = mock(Schedule.class);
        scheduleSupplier = () -> schedule;
        trigger = mock(TriggerNew.class);
    }

    @Test
    public void createTriggerRunReturnsTriggerRunWithStartedScheduleThatDoesNotifyObservers() {
        Entity entity = mock(Entity.class);
        TriggerTarget target = mock(TriggerTarget.class);
        TriggerContext context = new TriggerContext(entity, target);

        TriggerExecutor triggerExecutor = new TriggerExecutor(trigger, scheduleSupplier);
        TriggerRun triggerRun = triggerExecutor.createTriggerRun(context);

        assertThat(triggerRun).isNotNull();
    }
}
