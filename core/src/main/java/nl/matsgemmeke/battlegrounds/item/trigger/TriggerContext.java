package nl.matsgemmeke.battlegrounds.item.trigger;

import org.bukkit.entity.Entity;

public record TriggerContext(Entity entity, TriggerTarget target) {
}
