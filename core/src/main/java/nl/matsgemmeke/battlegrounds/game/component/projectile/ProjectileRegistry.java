package nl.matsgemmeke.battlegrounds.game.component.projectile;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProjectileRegistry {

    private final Set<UUID> projectileUniqueIds;

    public ProjectileRegistry() {
        this.projectileUniqueIds = new HashSet<>();
    }

    public boolean isRegistered(UUID uniqueId) {
        return projectileUniqueIds.contains(uniqueId);
    }

    public void register(UUID uniqueId) {
        projectileUniqueIds.add(uniqueId);
    }

    public void unregister(UUID uniqueId) {
        projectileUniqueIds.remove(uniqueId);
    }
}
