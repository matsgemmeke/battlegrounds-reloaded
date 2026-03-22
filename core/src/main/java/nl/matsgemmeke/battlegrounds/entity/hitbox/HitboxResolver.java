package nl.matsgemmeke.battlegrounds.entity.hitbox;

import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.*;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;

public class HitboxResolver {

    private static final HitboxProvider<Entity> FALLBACK_PROVIDER = new DefaultEntityHitboxProvider();
    private static final HitboxProvider<StaticBoundingBox> DEPLOYMENT_OBJECT_HITBOX_PROVIDER = new StaticBoundingBoxHitboxProvider();

    private final Map<EntityType, HitboxProvider<? extends Entity>> entityHitboxProviders;

    public HitboxResolver() {
        this.entityHitboxProviders = new HashMap<>();
    }

    public void addEntityHitboxProvider(EntityType entityType, HitboxProvider<? extends Entity> hitboxProvider) {
        entityHitboxProviders.put(entityType, hitboxProvider);
    }

    public HitboxProvider<StaticBoundingBox> resolveDeploymentObjectHitboxProvider() {
        return DEPLOYMENT_OBJECT_HITBOX_PROVIDER;
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> HitboxProvider<T> resolveHitboxProvider(T entity) {
        if (!entityHitboxProviders.containsKey(entity.getType())) {
            return (HitboxProvider<T>) FALLBACK_PROVIDER;
        }

        return (HitboxProvider<T>) entityHitboxProviders.get(entity.getType());
    }
}
