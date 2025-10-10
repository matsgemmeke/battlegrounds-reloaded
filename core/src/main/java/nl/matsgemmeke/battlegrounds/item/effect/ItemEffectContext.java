package nl.matsgemmeke.battlegrounds.item.effect;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public record ItemEffectContext(Entity entity, ItemEffectSource source, Location initiationLocation) {
}
