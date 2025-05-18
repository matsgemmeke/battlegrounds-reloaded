package nl.matsgemmeke.battlegrounds.item.trigger;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record TriggerContext(@NotNull Entity deployerEntity, @NotNull TriggerTarget target) {
}
