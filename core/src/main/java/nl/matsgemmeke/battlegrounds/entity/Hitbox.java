package nl.matsgemmeke.battlegrounds.entity;

import org.jetbrains.annotations.Nullable;

public enum Hitbox {

    HEAD(1.4, 1.8),
    LIMBS(0.0, 0.8),
    TORSO(0.8, 1.4);

    private double maxHeight;
    private double minHeight;

    Hitbox(double minHeight, double maxHeight) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Nullable
    public static Hitbox getHitbox(double eY, double pY) {
        double dif = pY - eY;

        for (Hitbox hitbox : Hitbox.values()) {
            if (dif >= hitbox.minHeight && dif < hitbox.maxHeight) {
                return hitbox;
            }
        }

        return null;
    }
}
