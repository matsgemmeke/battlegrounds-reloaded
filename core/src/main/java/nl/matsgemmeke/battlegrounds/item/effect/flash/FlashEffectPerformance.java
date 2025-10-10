package nl.matsgemmeke.battlegrounds.item.effect.flash;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlashEffectPerformance extends BaseItemEffectPerformance {

    private final FlashProperties properties;
    private final Map<GameEntity, PotionEffect> appliedPotionEffects;
    private final Scheduler scheduler;
    private final TargetFinder targetFinder;
    private Schedule cancelSchedule;

    @Inject
    public FlashEffectPerformance(Scheduler scheduler, TargetFinder targetFinder, @Assisted FlashProperties properties) {
        this.scheduler = scheduler;
        this.targetFinder = targetFinder;
        this.properties = properties;
        this.appliedPotionEffects = new HashMap<>();
    }

    @Override
    public boolean isPerforming() {
        return cancelSchedule != null && cancelSchedule.isRunning();
    }

    @Override
    public void perform(@NotNull ItemEffectContext context) {
        Entity entity = context.entity();
        ItemEffectSource source = context.source();

        this.createExplosionEffect(entity, source);
        this.applyPotionEffectToTargets(entity.getUniqueId(), source.getLocation());
        this.startCancelSchedule();

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
        for (GameEntity gameEntity : targetFinder.findTargets(entityId, location, properties.range())) {
            PotionEffectType potionEffectType = PotionEffectType.BLINDNESS;
            int duration = properties.potionEffect().duration();
            int amplifier = properties.potionEffect().amplifier();
            boolean ambient = properties.potionEffect().ambient();
            boolean particles = properties.potionEffect().particles();
            boolean icon = properties.potionEffect().icon();

            PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);

            gameEntity.getEntity().addPotionEffect(potionEffect);

            appliedPotionEffects.put(gameEntity, potionEffect);
        }
    }

    private void startCancelSchedule() {
        long delay = properties.potionEffect().duration();

        cancelSchedule = scheduler.createSingleRunSchedule(delay);
        cancelSchedule.addTask(this::rollback);
        cancelSchedule.start();
    }

    @Override
    public void rollback() {
        if (!this.isPerforming()) {
            return;
        }

        appliedPotionEffects.keySet().forEach(this::removePotionEffect);
        cancelSchedule.stop();
    }

    private void removePotionEffect(GameEntity gameEntity) {
        LivingEntity entity = gameEntity.getEntity();
        PotionEffect potionEffect = entity.getPotionEffect(PotionEffectType.BLINDNESS);

        // Only remove the potion effect is it's the same instance that the flash effect caused
        if (potionEffect != null && potionEffect == appliedPotionEffects.get(gameEntity)) {
            entity.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }
}
