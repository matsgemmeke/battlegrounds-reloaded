package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.Set;

/**
 * A fallback hitbox provider that creates a hitbox based on the entity's bounding box.
 */
public class BoundingBoxHitboxProvider implements HitboxProviderNew<Entity> {

    @Override
    public Hitbox provideHitbox(Entity entity) {
        Location baseLocation = entity.getLocation();
        BoundingBox boundingBox = entity.getBoundingBox();

        HitboxComponent component = new HitboxComponent(HitboxComponentType.TORSO, boundingBox.getWidthX(), boundingBox.getHeight(), boundingBox.getWidthZ(), 0, 0, 0);
        RelativeHitbox relativeHitbox = new RelativeHitbox(Set.of(component));

        return new Hitbox(baseLocation, relativeHitbox);
    }
}
