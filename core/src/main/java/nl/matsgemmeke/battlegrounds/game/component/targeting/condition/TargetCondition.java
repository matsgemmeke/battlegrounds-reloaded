package nl.matsgemmeke.battlegrounds.game.component.targeting.condition;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;

/**
 * A predicate used to determine whether a given damage target should be considered valid for a target finding result.
 *
 * <p>{@code TargetCondition} represents a single boolean rule that is applied to potential targets. Implementations
 * should be side effect free and return {@code true} if the entity satisfies the rule, or {@code false} otherwise.
 */
public interface TargetCondition {

    /**
     * Tests whether the given damage target should be included in the target finding result.
     *
     * @param target the target being evaluated
     * @param origin the origin location of the target finding query
     * @return       {@code true} if the entity should be included; {@code false} otherwise
     */
    boolean test(DamageTarget target, Location origin);
}
