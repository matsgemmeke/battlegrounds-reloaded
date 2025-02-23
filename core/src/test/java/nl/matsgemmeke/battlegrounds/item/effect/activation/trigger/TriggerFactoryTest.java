package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.enemy.EnemyProximityTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.floor.FloorHitTriggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerFactoryTest {

    private static final double CHECKING_RANGE = 2.5;
    private static final int PERIOD_BETWEEN_CHECKS = 10;

    private EnemyProximityTriggerFactory enemyProximityTriggerFactory;
    private FloorHitTriggerFactory floorHitTriggerFactory;
    private GameContextProvider contextProvider;
    private GameKey gameKey;

    @BeforeEach
    public void setUp() {
        enemyProximityTriggerFactory = mock(EnemyProximityTriggerFactory.class);
        floorHitTriggerFactory = mock(FloorHitTriggerFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
    }

    @Test
    public void throwExceptionWhenGivenTypeIsInvalid() {
        Map<String, Object> triggerConfig = Map.of("type", "fail");

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory);

        assertThrows(InvalidItemConfigurationException.class, () -> factory.create(gameKey, triggerConfig));
    }

    @Test
    public void createInstanceOfEnemyProximityTrigger() {
        Map<String, Object> triggerConfig = Map.of(
                "type", "ENEMY_PROXIMITY",
                "checking-range", CHECKING_RANGE,
                "period-between-checks", PERIOD_BETWEEN_CHECKS
        );

        TargetFinder targetFinder = mock(TargetFinder.class);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        EnemyProximityTrigger enemyProximityTrigger = mock(EnemyProximityTrigger.class);
        when(enemyProximityTriggerFactory.create(targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS)).thenReturn(enemyProximityTrigger);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory);
        Trigger trigger = factory.create(gameKey, triggerConfig);

        assertEquals(enemyProximityTrigger, trigger);
    }

    @Test
    public void createInstanceOfFloorHitTrigger() {
        Map<String, Object> triggerConfig = Map.of(
                "type", "FLOOR_HIT",
                "period-between-checks", PERIOD_BETWEEN_CHECKS
        );

        FloorHitTrigger floorHitTrigger = mock(FloorHitTrigger.class);
        when(floorHitTriggerFactory.create(PERIOD_BETWEEN_CHECKS)).thenReturn(floorHitTrigger);

        TriggerFactory factory = new TriggerFactory(contextProvider, enemyProximityTriggerFactory, floorHitTriggerFactory);
        Trigger trigger = factory.create(gameKey, triggerConfig);

        assertEquals(floorHitTrigger, trigger);
    }
}
