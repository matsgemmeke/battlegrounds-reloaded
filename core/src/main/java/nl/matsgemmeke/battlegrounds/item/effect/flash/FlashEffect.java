package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlashEffect extends BaseItemEffect {

    @NotNull
    private FlashProperties properties;
    @NotNull
    private Map<GameEntity, PotionEffect> appliedPotionEffects;
    @NotNull
    private TargetFinder targetFinder;

    public FlashEffect(@NotNull FlashProperties properties, @NotNull TargetFinder targetFinder) {
        this.properties = properties;
        this.targetFinder = targetFinder;
        this.appliedPotionEffects = new HashMap<>();
    }

    public void perform(@NotNull ItemEffectContext context) {
        Entity entity = context.getEntity();
        ItemEffectSource source = context.getSource();

        this.createExplosionEffect(entity, source);
        this.applyPotionEffectToTargets(entity.getUniqueId(), source.getLocation());

        source.remove();
    }

    private void createExplosionEffect(@NotNull Entity damageSource, @NotNull ItemEffectSource source) {
        float power = properties.power();
        boolean setFire = properties.setFire();
        boolean breakBlocks = properties.breakBlocks();

        World world = source.getWorld();
        Location location = source.getLocation();

        world.createExplosion(location, power, setFire, breakBlocks, damageSource);
    }

    private void applyPotionEffectToTargets(@NotNull UUID entityId, @NotNull Location location) {
        for (GameEntity target : targetFinder.findTargets(entityId, location, properties.range())) {
            PotionEffectType potionEffectType = PotionEffectType.BLINDNESS;
            int duration = properties.potionEffect().duration();
            int amplifier = properties.potionEffect().amplifier();
            boolean ambient = properties.potionEffect().ambient();
            boolean particles = properties.potionEffect().particles();
            boolean icon = properties.potionEffect().icon();

            PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);

            target.getEntity().addPotionEffect(potionEffect);

            appliedPotionEffects.put(target, potionEffect);
        }
    }

    public void reset() {
        for (GameEntity target : appliedPotionEffects.keySet()) {
            LivingEntity entity = target.getEntity();
            PotionEffect potionEffect = entity.getPotionEffect(PotionEffectType.BLINDNESS);

            // Only remove the potion effect is it's the same instance that the flash effect caused
            if (potionEffect != null && potionEffect == appliedPotionEffects.get(target)) {
                entity.removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }
    }
}
