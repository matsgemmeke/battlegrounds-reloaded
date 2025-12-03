package nl.matsgemmeke.battlegrounds.item.effect.flash;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.PotionEffectReceiver;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlashEffectPerformance extends BaseItemEffectPerformance {

    private final FlashProperties properties;
    private final Map<PotionEffectReceiver, PotionEffect> appliedPotionEffects;
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
        Entity entity = context.getEntity();
        ItemEffectSource source = context.getSource();

        this.createExplosionEffect(entity, source);
        this.applyPotionEffectToTargets(source.getLocation());
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

    private void applyPotionEffectToTargets(Location location) {
        for (PotionEffectReceiver potionEffectReceiver : targetFinder.findPotionEffectReceivers(location, properties.range())) {
            PotionEffectType potionEffectType = PotionEffectType.BLINDNESS;
            int duration = properties.potionEffect().duration();
            int amplifier = properties.potionEffect().amplifier();
            boolean ambient = properties.potionEffect().ambient();
            boolean particles = properties.potionEffect().particles();
            boolean icon = properties.potionEffect().icon();

            PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);

            potionEffectReceiver.addPotionEffect(potionEffect);

            appliedPotionEffects.put(potionEffectReceiver, potionEffect);
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

    private void removePotionEffect(PotionEffectReceiver potionEffectReceiver) {
        PotionEffect potionEffect = potionEffectReceiver.getPotionEffect(PotionEffectType.BLINDNESS).orElse(null);

        // Only remove the potion effect is it's the same instance that the flash effect caused
        if (potionEffect != null && potionEffect == appliedPotionEffects.get(potionEffectReceiver)) {
            potionEffectReceiver.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }
}
