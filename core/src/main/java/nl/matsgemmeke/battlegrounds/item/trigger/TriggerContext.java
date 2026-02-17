package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;

import java.util.UUID;

/**
 * Carries context data for trigger implementations.
 *
 * @param sourceId the id of the source that activated the trigger
 * @param actor    the actor which the trigger is monitoring
 */
public record TriggerContext(UUID sourceId, Actor actor) {

    public TriggerContext withActor(Actor actor) {
        return new TriggerContext(sourceId, actor);
    }
}
