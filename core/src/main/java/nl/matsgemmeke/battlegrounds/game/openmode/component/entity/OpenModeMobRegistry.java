package nl.matsgemmeke.battlegrounds.game.openmode.component.entity;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.EntityKeyRegistry;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.OpenModeEntity;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OpenModeMobRegistry implements MobRegistry {

    private final EntityKeyRegistry entityKeyRegistry;
    private final HitboxResolver hitboxResolver;
    private final Map<UUID, GameMob> mobs;

    @Inject
    public OpenModeMobRegistry(EntityKeyRegistry entityKeyRegistry, HitboxResolver hitboxResolver) {
        this.entityKeyRegistry = entityKeyRegistry;
        this.hitboxResolver = hitboxResolver;
        this.mobs = new HashMap<>();
    }

    @Override
    public Optional<GameMob> findByUniqueId(UUID uniqueId) {
        return Optional.ofNullable(mobs.get(uniqueId));
    }

    @Override
    public GameMob register(LivingEntity entity) {
        UUID uniqueId = entity.getUniqueId();
        GameMob existingMob = mobs.get(uniqueId);

        if (existingMob != null) {
            return existingMob;
        }

        HitboxProvider<LivingEntity> hitboxProvider = hitboxResolver.resolveHitboxProvider(entity);
        OpenModeEntity openModeEntity = new OpenModeEntity(entity, hitboxProvider);

        mobs.put(uniqueId, openModeEntity);

        EntityKey entityKey = EntityKey.fromEntityType(entity.getType());
        entityKeyRegistry.register(uniqueId, entityKey);

        return openModeEntity;
    }
}
