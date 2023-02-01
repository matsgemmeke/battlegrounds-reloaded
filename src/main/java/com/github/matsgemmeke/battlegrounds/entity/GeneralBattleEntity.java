package com.github.matsgemmeke.battlegrounds.entity;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GeneralBattleEntity implements BattleEntity {

    private static final double RELATIVE_ACCURACY = 1.0;

    @NotNull
    private LivingEntity entity;

    public GeneralBattleEntity(@NotNull LivingEntity entity) {
        this.entity = entity;
    }

    @NotNull
    public LivingEntity getEntity() {
        return entity;
    }

    public double damage(double damageAmount) {
        if (entity.isDead() || entity.getHealth() <= 0.0) {
            return 0.0;
        }

        // Divide by 5 to convert to hearts value
        double finalHealth = entity.getHealth() - damageAmount / 5;

        System.out.println(entity.getHealth());
        System.out.println(finalHealth);

        // Create fake damage animation
        entity.damage(0.001);
        // Set the health to 0 if the damage is greater than the health
        entity.setHealth(finalHealth > 0.0 ? finalHealth : 0);

        return entity.getHealth();
    }

    public double getRelativeAccuracy() {
        return RELATIVE_ACCURACY;
    }
}
