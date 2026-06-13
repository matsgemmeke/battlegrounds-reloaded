package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.entity.damage.Damage;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.UUID;

public class OpenModeEntity implements GameMob {

    private final EntityKey entityKey;
    private final HitboxProvider<LivingEntity> hitboxProvider;
    private final LivingEntity entity;

    public OpenModeEntity(LivingEntity entity, EntityKey entityKey, HitboxProvider<LivingEntity> hitboxProvider) {
        this.entity = entity;
        this.entityKey = entityKey;
        this.hitboxProvider = hitboxProvider;
    }

    @Override
    public EntityKey getEntityKey() {
        return entityKey;
    }

    @Override
    public double getHealth() {
        return entity.getHealth();
    }

    @Override
    public void setHealth(double health) {
        entity.setHealth(health);
    }

    @Override
    public Location getLocation() {
        return entity.getLocation();
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public UUID getUniqueId() {
        return entity.getUniqueId();
    }

    @Override
    public Vector getVelocity() {
        return entity.getVelocity();
    }

    @Override
    public World getWorld() {
        return entity.getWorld();
    }

    @Override
    public boolean isValid() {
        return entity.isValid();
    }

    @Override
    public void addPotionEffect(PotionEffect potionEffect) {
        entity.addPotionEffect(potionEffect);
    }

    @Override
    public Optional<PotionEffect> getPotionEffect(PotionEffectType potionEffectType) {
        return Optional.ofNullable(entity.getPotionEffect(potionEffectType));
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        entity.removePotionEffect(potionEffectType);
    }

    @Override
    public double damage(Damage damage) {
        if (entity.isDead() || entity.getHealth() <= 0.0) {
            return 0.0;
        }

        // Divide by 5 to convert to hearts value
        double finalHealth = Math.max(entity.getHealth() - damage.amount() / 5, 0.0);

        // Create fake damage animation
        entity.damage(0.001);
        // Set the health to 0 if the damage is greater than the health
        entity.setHealth(finalHealth);

        return damage.amount();
    }

    @Override
    public Hitbox getHitbox() {
        return hitboxProvider.provideHitbox(entity);
    }
}
