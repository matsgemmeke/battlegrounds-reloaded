package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.projectile.ItemProjectile;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ProjectileLauncher} that uses {@link Item} entities in their launch.
 */
public class ItemLauncher implements ProjectileLauncher {

    private static final int ITEM_PICKUP_DELAY = 10000;

    private final AudioEmitter audioEmitter;
    private final ItemEffect itemEffect;
    private final ItemLaunchProperties properties;
    private final Scheduler scheduler;
    private final Set<Schedule> soundPlaySchedules;
    private final Set<TriggerExecutor> triggerExecutors;

    @Inject
    public ItemLauncher(AudioEmitter audioEmitter, Scheduler scheduler, @Assisted ItemEffect itemEffect, @Assisted ItemLaunchProperties properties) {
        this.audioEmitter = audioEmitter;
        this.scheduler = scheduler;
        this.itemEffect = itemEffect;
        this.properties = properties;
        this.soundPlaySchedules = new HashSet<>();
        this.triggerExecutors = new HashSet<>();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    @Override
    public void cancel() {
        soundPlaySchedules.forEach(Schedule::stop);
    }

    @Override
    public void launch(LaunchContext context) {
        Entity entity = context.entity();
        World world = context.world();
        Location dropLocation = context.direction();
        Vector velocity = context.direction().getDirection().multiply(properties.velocity());

        ItemStack itemStack = properties.itemTemplate().createItemStack();

        Item item = world.dropItem(dropLocation, itemStack);
        item.setPickupDelay(ITEM_PICKUP_DELAY);
        item.setVelocity(velocity);

        ItemProjectile projectile = new ItemProjectile(item);
        TriggerContext triggerContext = new TriggerContext(entity, projectile);

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
            triggerRun.addObserver(() -> this.startItemEffect(entity, projectile, dropLocation));
            triggerRun.start();
        }

        this.scheduleSoundPlayTasks(properties.shotSounds(), entity);
    }

    private void startItemEffect(Entity entity, ItemProjectile projectile, Location initiationLocation) {
        ItemEffectContext effectContext = new ItemEffectContext(entity, projectile, initiationLocation);

        itemEffect.startPerformance(effectContext);
    }

    private void scheduleSoundPlayTasks(List<GameSound> sounds, Entity entity) {
        for (GameSound sound : sounds) {
            Schedule schedule = scheduler.createSingleRunSchedule(sound.getDelay());
            schedule.addTask(() -> audioEmitter.playSound(sound, entity.getLocation()));
            schedule.start();

            soundPlaySchedules.add(schedule);
        }
    }
}
