package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriggerExecutorTest {

    private static final UUID SOURCE_ID = UUID.randomUUID();

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
        Actor actor = mock(Actor.class);
        TriggerContext context = new TriggerContext(SOURCE_ID, actor);

        triggerExecutor.setRepeating(true);

        TriggerRun triggerRun = triggerExecutor.createTriggerRun(context);

        assertThat(triggerRun.isRepeating()).isTrue();
    }
}
