package nl.matsgemmeke.battlegrounds.item.effect.trigger;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.activator.ActivatorTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.enemy.EnemyProximityTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.floor.FloorHitTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.timed.TimedTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.timed.TimedTriggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerFactoryTest {

    private static final double CHECKING_RANGE = 2.5;
    private static final long DELAY_UNTIL_ACTIVATION = 10L;
    private static final long PERIOD_BETWEEN_CHECKS = 2L;

    private EnemyProximityTriggerFactory enemyProximityTriggerFactory;
    private FloorHitTriggerFactory floorHitTriggerFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private TimedTriggerFactory timedTriggerFactory;

    @BeforeEach
    public void setUp() {
        enemyProximityTriggerFactory = mock(EnemyProximityTriggerFactory.class);
        floorHitTriggerFactory = mock(FloorHitTriggerFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
        timedTriggerFactory = mock(TimedTriggerFactory.class);
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsActivatorAndGivenActivatorIsNull() {
        TriggerSpec spec = new TriggerSpec("ACTIVATOR", null, null, null);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey, null))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create ActivatorTrigger: given activator object is null");
    }

    @Test
    public void createReturnsActivatorTriggerInstanceWhenTriggerTypeEqualsActivator() {
        TriggerSpec spec = new TriggerSpec("ACTIVATOR", null, null, null);
        Activator activator = mock(Activator.class);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);
        Trigger trigger = factory.create(spec, gameKey, activator);

        assertThat(trigger).isInstanceOf(ActivatorTrigger.class);
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsEnemyProximityAndCheckRangeIsNull() {
        TriggerSpec spec = new TriggerSpec("ENEMY_PROXIMITY", null, PERIOD_BETWEEN_CHECKS, null);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey, null))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create EnemyProximityTrigger because of invalid spec: Required 'checkRange' value is missing");
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsEnemyProximityAndCheckIntervalIsNull() {
        TriggerSpec spec = new TriggerSpec("ENEMY_PROXIMITY", CHECKING_RANGE, null, null);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey, null))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create EnemyProximityTrigger because of invalid spec: Required 'checkInterval' value is missing");
    }

    @Test
    public void createReturnsEnemyProximityTriggerInstanceWhenTripperTypeEqualsEnemyProximity() {
        TriggerSpec spec = new TriggerSpec("ENEMY_PROXIMITY", CHECKING_RANGE, PERIOD_BETWEEN_CHECKS, null);

        TargetFinder targetFinder = mock(TargetFinder.class);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        EnemyProximityTrigger enemyProximityTrigger = mock(EnemyProximityTrigger.class);
        when(enemyProximityTriggerFactory.create(targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS)).thenReturn(enemyProximityTrigger);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);
        Trigger trigger = factory.create(spec, gameKey, null);

        assertThat(trigger).isEqualTo(enemyProximityTrigger);
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsFloorHitAndCheckIntervalIsNull() {
        TriggerSpec spec = new TriggerSpec("FLOOR_HIT", null, null, null);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey, null))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create FloorHitTrigger because of invalid spec: Required 'checkInterval' value is missing");
    }

    @Test
    public void createReturnsFloorHitTriggerInstanceWhenTriggerTypeEqualsFloorHit() {
        TriggerSpec spec = new TriggerSpec("FLOOR_HIT", null, PERIOD_BETWEEN_CHECKS, null);

        FloorHitTrigger floorHitTrigger = mock(FloorHitTrigger.class);
        when(floorHitTriggerFactory.create(PERIOD_BETWEEN_CHECKS)).thenReturn(floorHitTrigger);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);
        Trigger trigger = factory.create(spec, gameKey, null);

        assertThat(trigger).isEqualTo(floorHitTrigger);
    }

    @Test
    public void createThrowsTriggerCreationExceptionWhenTriggerTypeEqualsTimedAndDelayUntilActivationIsNull() {
        TriggerSpec spec = new TriggerSpec("TIMED", null, null, null);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);

        assertThatThrownBy(() -> factory.create(spec, gameKey, null))
                .isInstanceOf(TriggerCreationException.class)
                .hasMessage("Cannot create TimedTrigger because of invalid spec: Required 'delayUntilActivation' value is missing");
    }

    @Test
    public void createReturnsTimedTriggerInstanceWhenTriggerTypeEqualsTimed() {
        TriggerSpec spec = new TriggerSpec("TIMED", null, null, DELAY_UNTIL_ACTIVATION);

        TimedTrigger timedTrigger = mock(TimedTrigger.class);
        when(timedTriggerFactory.create(DELAY_UNTIL_ACTIVATION)).thenReturn(timedTrigger);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory, timedTriggerFactory);
        Trigger trigger = factory.create(spec, gameKey, null);

        assertThat(trigger).isEqualTo(timedTrigger);
    }
}
