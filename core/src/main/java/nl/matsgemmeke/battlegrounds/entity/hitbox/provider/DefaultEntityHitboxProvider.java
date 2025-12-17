package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxUtil;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

/**
 * A fallback hitbox provider that creates a hitbox based on the entity's bounding box.
 */
public class DefaultEntityHitboxProvider implements HitboxProvider<Entity> {

    @Override
    public Hitbox provideHitbox(Entity entity) {
        Location baseLocation = entity.getLocation();
        BoundingBox boundingBox = entity.getBoundingBox();
        StaticBoundingBox staticBoundingBox = new StaticBoundingBox(baseLocation, boundingBox.getWidthX(), boundingBox.getHeight(), boundingBox.getWidthZ());

        return HitboxUtil.createHitbox(staticBoundingBox);
    }
}
