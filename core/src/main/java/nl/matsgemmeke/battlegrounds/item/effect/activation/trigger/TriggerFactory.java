package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TriggerFactory {

    @NotNull
    private TaskRunner taskRunner;

    public TriggerFactory(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    @NotNull
    public Trigger make(@NotNull GameContext context, @NotNull Map<String, Object> triggerConfig) {
        String triggerTypeValue = (String) triggerConfig.get("type");
        TriggerType triggerType;

        try {
            triggerType = TriggerType.valueOf(triggerTypeValue);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemConfigurationException("Trigger type \"" + triggerTypeValue + "\" is invalid!");
        }

        switch (triggerType) {
            case ENEMY_PROXIMITY -> {
                TargetFinder targetFinder = context.getTargetFinder();

                double checkingRange = (double) triggerConfig.get("checking-range");
                long periodBetweenChecks = (int) triggerConfig.get("period-between-checks");

                return new EnemyProximityTrigger(targetFinder, taskRunner, checkingRange, periodBetweenChecks);
            }
            case FLOOR_HIT -> {
                long periodBetweenChecks = (int) triggerConfig.get("period-between-checks");

                return new FloorHitTrigger(taskRunner, periodBetweenChecks);
            }
        }

        throw new InvalidItemConfigurationException("Unknown equipment trigger type \"" + triggerTypeValue + "\"!");
    }
}
