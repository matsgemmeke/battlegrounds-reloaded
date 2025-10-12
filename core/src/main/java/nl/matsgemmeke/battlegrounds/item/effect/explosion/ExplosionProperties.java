package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.item.RangeProfile;

public record ExplosionProperties(RangeProfile rangeProfile, float power, boolean setFire, boolean breakBlocks) {
}
