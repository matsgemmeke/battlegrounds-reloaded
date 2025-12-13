package nl.matsgemmeke.battlegrounds.game.openmode.component.entity;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.OpenModeEntity;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProviderNew;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OpenModeMobRegistry implements MobRegistry {

    private final HitboxResolver hitboxResolver;
    private final Map<UUID, GameMob> mobs;

    @Inject
    public OpenModeMobRegistry(HitboxResolver hitboxResolver) {
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

        HitboxProviderNew<LivingEntity> hitboxProvider = hitboxResolver.resolveHitboxProviderNew(entity);
        OpenModeEntity openModeEntity = new OpenModeEntity(entity, hitboxProvider);

        mobs.put(uniqueId, openModeEntity);

        return openModeEntity;
    }
}
