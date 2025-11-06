package nl.matsgemmeke.battlegrounds.game.openmode.component.entity;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.OpenModeEntity;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.resolver.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.component.entity.LivingEntityRegistry;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OpenModeLivingEntityRegistry implements LivingEntityRegistry {

    private final HitboxResolver hitboxResolver;
    private final Map<UUID, GameEntity> livingEntities;

    @Inject
    public OpenModeLivingEntityRegistry(HitboxResolver hitboxResolver) {
        this.hitboxResolver = hitboxResolver;
        this.livingEntities = new HashMap<>();
    }

    @Override
    public Optional<GameEntity> findByUniqueId(UUID uniqueId) {
        return Optional.ofNullable(livingEntities.get(uniqueId));
    }

    @Override
    public GameEntity register(LivingEntity entity) {
        UUID uniqueId = entity.getUniqueId();
        GameEntity existingEntity = livingEntities.get(uniqueId);

        if (existingEntity != null) {
            return existingEntity;
        }

        Hitbox hitbox = hitboxResolver.resolveHitbox(entity).orElseThrow();
        OpenModeEntity openModeEntity = new OpenModeEntity(entity, hitbox);

        livingEntities.put(uniqueId, openModeEntity);

        return openModeEntity;
    }
}
