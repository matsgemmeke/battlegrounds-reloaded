package nl.matsgemmeke.battlegrounds.item.effect.trigger;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record TriggerContext(@NotNull Entity deployerEntity, @NotNull TriggerTarget target) {
}
