package nl.matsgemmeke.battlegrounds.item.trigger;

import java.util.UUID;

/**
 * Carries context data for trigger implementations.
 *
 * @param sourceId the id of the source that activated the trigger
 * @param target   the target which the trigger is monitoring
 */
public record TriggerContext(UUID sourceId, TriggerTarget target) {
}
