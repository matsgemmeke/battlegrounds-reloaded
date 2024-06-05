package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class TrainingModeEntity implements GameEntity {

    @NotNull
    private LivingEntity entity;

    public TrainingModeEntity(@NotNull LivingEntity entity) {
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
        double finalHealth = Math.max(entity.getHealth() - damageAmount / 5, 0.0);

        // Create fake damage animation
        entity.damage(0.001);
        // Set the health to 0 if the damage is greater than the health
        entity.setHealth(finalHealth);

        return finalHealth;
    }
}
