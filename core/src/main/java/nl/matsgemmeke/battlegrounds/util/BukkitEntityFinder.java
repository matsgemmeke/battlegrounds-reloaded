package nl.matsgemmeke.battlegrounds.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class BukkitEntityFinder {

    public Optional<Entity> getEntityByUniqueId(UUID uniqueId) {
        Entity entity = Bukkit.getEntity(uniqueId);
        return Optional.ofNullable(entity);
    }
}
