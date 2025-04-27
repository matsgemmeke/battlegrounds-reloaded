package nl.matsgemmeke.battlegrounds.item.effect.trigger;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.activator.ActivatorTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.enemy.EnemyProximityTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.floor.FloorHitTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.timed.TimedTriggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TriggerFactory {

    @NotNull
    private final EnemyProximityTriggerFactory enemyProximityTriggerFactory;
    @NotNull
    private final FloorHitTriggerFactory floorHitTriggerFactory;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final TimedTriggerFactory timedTriggerFactory;

    @Inject
    public TriggerFactory(
            @NotNull GameContextProvider contextProvider,
            @NotNull EnemyProximityTriggerFactory enemyProximityTriggerFactory,
            @NotNull FloorHitTriggerFactory floorHitTriggerFactory,
            @NotNull TimedTriggerFactory timedTriggerFactory
    ) {
        this.contextProvider = contextProvider;
        this.enemyProximityTriggerFactory = enemyProximityTriggerFactory;
        this.floorHitTriggerFactory = floorHitTriggerFactory;
        this.timedTriggerFactory = timedTriggerFactory;
    }

    @NotNull
    public Trigger create(@NotNull TriggerSpec spec, @NotNull GameKey gameKey, @Nullable Activator activator) {
        TriggerType triggerType = TriggerType.valueOf(spec.type());

        switch (triggerType) {
            case ACTIVATOR -> {
                if (activator == null) {
                    throw new TriggerCreationException("Cannot create ActivatorTrigger: given activator object is null");
                }

                return new ActivatorTrigger(activator);
            }
            case ENEMY_PROXIMITY -> {
                // The spec is supposed to be valid, but perform double checks
                double checkingRange = this.validateNotNull(spec.checkingRange(), "checkingRange", "EnemyProximityTrigger");
                long periodBetweenChecks = this.validateNotNull(spec.periodBetweenChecks(), "periodBetweenChecks", "EnemyProximityTrigger");

                TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

                return enemyProximityTriggerFactory.create(targetFinder, checkingRange, periodBetweenChecks);
            }
            case FLOOR_HIT -> {
                // The spec is supposed to be valid, but perform double checks
                long periodBetweenChecks = this.validateNotNull(spec.periodBetweenChecks(), "periodBetweenChecks", "FloorHitTrigger");

                return floorHitTriggerFactory.create(periodBetweenChecks);
            }
            case TIMED -> {
                // The spec is supposed to be valid, but perform double checks
                long delayUntilActivation = this.validateNotNull(spec.delayUntilActivation(), "delayUntilActivation", "TimedTrigger");

                return timedTriggerFactory.create(delayUntilActivation);
            }
        }

        throw new TriggerCreationException("Unknown trigger type '%s'".formatted(spec.type()));
    }

    private <T> T validateNotNull(@Nullable T value, @NotNull String valueName, @NotNull String triggerName) {
        if (value == null) {
            throw new TriggerCreationException("Cannot create %s because of invalid spec: Required '%s' value is missing".formatted(triggerName, valueName));
        }

        return value;
    }
}
