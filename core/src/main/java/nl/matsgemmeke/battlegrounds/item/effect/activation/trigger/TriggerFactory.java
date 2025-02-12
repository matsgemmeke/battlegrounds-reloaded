package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.enemy.EnemyProximityTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.floor.FloorHitTriggerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TriggerFactory {

    @NotNull
    private final EnemyProximityTriggerFactory enemyProximityTriggerFactory;
    @NotNull
    private final FloorHitTriggerFactory floorHitTriggerFactory;
    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public TriggerFactory(
            @NotNull GameContextProvider contextProvider,
            @NotNull EnemyProximityTriggerFactory enemyProximityTriggerFactory,
            @NotNull FloorHitTriggerFactory floorHitTriggerFactory
    ) {
        this.contextProvider = contextProvider;
        this.enemyProximityTriggerFactory = enemyProximityTriggerFactory;
        this.floorHitTriggerFactory = floorHitTriggerFactory;
    }

    @NotNull
    public Trigger create(@NotNull GameKey gameKey, @NotNull Map<String, Object> triggerConfig) {
        String triggerTypeValue = (String) triggerConfig.get("type");
        TriggerType triggerType;

        try {
            triggerType = TriggerType.valueOf(triggerTypeValue);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemConfigurationException("Trigger type \"" + triggerTypeValue + "\" is invalid!");
        }

        switch (triggerType) {
            case ENEMY_PROXIMITY -> {
                TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

                double checkingRange = (double) triggerConfig.get("checking-range");
                long periodBetweenChecks = (int) triggerConfig.get("period-between-checks");

                return enemyProximityTriggerFactory.create(targetFinder, checkingRange, periodBetweenChecks);
            }
            case FLOOR_HIT -> {
                long periodBetweenChecks = (int) triggerConfig.get("period-between-checks");

                return floorHitTriggerFactory.create(periodBetweenChecks);
            }
        }

        throw new InvalidItemConfigurationException("Unknown equipment trigger type \"" + triggerTypeValue + "\"!");
    }
}
