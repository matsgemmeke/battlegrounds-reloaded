package nl.matsgemmeke.battlegrounds.item.effect.trigger;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerCreationException;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerFactory;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.trigger.timed.TimedTrigger;
import nl.matsgemmeke.battlegrounds.item.trigger.timed.TimedTriggerFactory;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerFactoryTest {

    private static final double RANGE = 2.5;
    private static final long DELAY = 10L;
    private static final long INTERVAL = 2L;
    private static final List<Long> OFFSET_DELAYS = List.of(10L, 20L);

    private EnemyProximityTriggerFactory enemyProximityTriggerFactory;
    private FloorHitTriggerFactory floorHitTriggerFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private Scheduler scheduler;
    private TimedTriggerFactory timedTriggerFactory;

    @BeforeEach
    public void setUp() {
        enemyProximityTriggerFactory = mock(EnemyProximityTriggerFactory.class);
        floorHitTriggerFactory = mock(FloorHitTriggerFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
        scheduler = mock(Scheduler.class);
        timedTriggerFactory = mock(TimedTriggerFactory.class);
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsEnemyProximityAndRangeIsNull() {
        TriggerSpec spec = new TriggerSpec("ENEMY_PROXIMITY", DELAY, INTERVAL, null, null);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, scheduler, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create trigger ENEMY_PROXIMITY because of invalid spec: Required 'range' value is missing");
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsEnemyProximityAndIntervalIsNull() {
        TriggerSpec spec = new TriggerSpec("ENEMY_PROXIMITY", DELAY, null, null, RANGE);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, scheduler, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create trigger ENEMY_PROXIMITY because of invalid spec: Required 'interval' value is missing");
    }

    @Test
    public void createReturnsEnemyProximityTriggerInstanceWhenTripperTypeEqualsEnemyProximity() {
        TriggerSpec spec = new TriggerSpec("ENEMY_PROXIMITY", DELAY, INTERVAL, null, RANGE);

        TargetFinder targetFinder = mock(TargetFinder.class);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        EnemyProximityTrigger enemyProximityTrigger = mock(EnemyProximityTrigger.class);
        when(enemyProximityTriggerFactory.create(targetFinder, RANGE, INTERVAL)).thenReturn(enemyProximityTrigger);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, scheduler, timedTriggerFactory);
        Trigger trigger = factory.create(spec, gameKey);

        assertThat(trigger).isEqualTo(enemyProximityTrigger);
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsFloorHitAndIntervalIsNull() {
        TriggerSpec spec = new TriggerSpec("FLOOR_HIT", DELAY, null, null, RANGE);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, scheduler, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create trigger FLOOR_HIT because of invalid spec: Required 'interval' value is missing");
    }

    @Test
    public void createReturnsFloorHitTriggerInstanceWhenTriggerTypeEqualsFloorHit() {
        TriggerSpec spec = new TriggerSpec("FLOOR_HIT", DELAY, INTERVAL, null, RANGE);

        FloorHitTrigger floorHitTrigger = mock(FloorHitTrigger.class);
        when(floorHitTriggerFactory.create(INTERVAL)).thenReturn(floorHitTrigger);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, scheduler, timedTriggerFactory);
        Trigger trigger = factory.create(spec, gameKey);

        assertThat(trigger).isEqualTo(floorHitTrigger);
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsTimedAndDelayIsNull() {
        TriggerSpec spec = new TriggerSpec("TIMED", null, null, null, null);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, scheduler, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create trigger TIMED because of invalid spec: Required 'delay' value is missing");
    }

    @Test
    public void createReturnsTimedTriggerInstanceWhenTriggerTypeEqualsTimed() {
        TriggerSpec spec = new TriggerSpec("TIMED", DELAY, null, null, null);

        TimedTrigger timedTrigger = mock(TimedTrigger.class);
        when(timedTriggerFactory.create(DELAY)).thenReturn(timedTrigger);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, scheduler, timedTriggerFactory);
        Trigger trigger = factory.create(spec, gameKey);

        assertThat(trigger).isEqualTo(timedTrigger);
    }
}
