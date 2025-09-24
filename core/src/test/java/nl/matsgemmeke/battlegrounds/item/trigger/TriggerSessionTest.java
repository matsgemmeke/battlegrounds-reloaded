package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TriggerSessionTest {

    private Schedule schedule;

    @BeforeEach
    public void setUp() {
        schedule = mock(Schedule.class);
    }

    @Test
    public void cancelStopsSchedule() {
        TriggerSession session = new TriggerSession(schedule);
        session.cancel();

        verify(schedule).stop();
    }

    @Test
    public void notifyObserversNotifiesObservers() {
        TriggerObserver observer = mock(TriggerObserver.class);

        TriggerSession session = new TriggerSession(schedule);
        session.addObserver(observer);
        session.notifyObservers();

        verify(observer).onActivate();
    }

    @Test
    public void startStartsSchedule() {
        TriggerSession session = new TriggerSession(schedule);
        session.start();

        verify(schedule).start();
    }
}
