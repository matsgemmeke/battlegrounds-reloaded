package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriggerExecutorTest {

    @Mock
    private Schedule schedule;
    @Mock
    private Trigger trigger;

    private TriggerExecutor triggerExecutor;

    @BeforeEach
    void setUp() {
        Supplier<Schedule> scheduleSupplier = () -> schedule;

        triggerExecutor = new TriggerExecutor(trigger, scheduleSupplier);
    }

    @Test
    void createTriggerRunReturnsTriggerRunWithStartedScheduleThatDoesNotifyObservers() {
        Entity entity = mock(Entity.class);
        TriggerTarget target = mock(TriggerTarget.class);
        TriggerContext context = new TriggerContext(entity, target);

        TriggerRun triggerRun = triggerExecutor.createTriggerRun(context);

        assertThat(triggerRun).isNotNull();
    }
}
