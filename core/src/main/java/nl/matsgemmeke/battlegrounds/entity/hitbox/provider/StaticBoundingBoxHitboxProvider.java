package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxUtil;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;

public class StaticBoundingBoxHitboxProvider implements HitboxProvider<StaticBoundingBox> {

    @Override
    public Hitbox provideHitbox(StaticBoundingBox boundingBox) {
        return HitboxUtil.createHitbox(boundingBox);
    }
}
