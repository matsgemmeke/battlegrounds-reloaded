package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.ItemActor;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Implementation of {@link ProjectileLauncher} that uses {@link Item} entities in their launch.
 */
public class ItemLauncher implements ProjectileLauncher {

    private final CollisionResultAdapter collisionResultAdapter;
    private final ItemEffect itemEffect;
    private final ItemLaunchProperties properties;
    private final Set<SoundPlayback> soundPlaybacks;
    private final Set<TriggerExecutor> triggerExecutors;
    private final SoundEmitter soundEmitter;

    @Inject
    public ItemLauncher(CollisionResultAdapter collisionResultAdapter, SoundEmitter soundEmitter, @Assisted ItemEffect itemEffect, @Assisted ItemLaunchProperties properties) {
        this.collisionResultAdapter = collisionResultAdapter;
        this.soundEmitter = soundEmitter;
        this.itemEffect = itemEffect;
        this.properties = properties;
        this.soundPlaybacks = new HashSet<>();
        this.triggerExecutors = new HashSet<>();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    @Override
    public void cancel() {
        soundPlaybacks.forEach(SoundPlayback::cancel);
        soundPlaybacks.clear();
    }

    @Override
    public void launch(LaunchContext context) {
        String itemName = context.itemName();
        DamageSource damageSource = context.damageSource();
        UUID damageSourceId = damageSource.getUniqueId();
        World world = context.world();
        Location dropLocation = context.direction();
        Supplier<Location> soundLocationSupplier = context.soundLocationSupplier();

        ItemStack itemStack = properties.itemTemplate().createItemStack();
        int pickupDelay = properties.pickupDelay();
        Vector velocity = context.direction().getDirection().multiply(properties.velocity());

        Item item = world.dropItem(dropLocation, itemStack);
        item.setPickupDelay(pickupDelay);
        item.setVelocity(velocity);

        ItemActor actor = new ItemActor(item);
        TriggerContext triggerContext = new TriggerContext(damageSourceId, actor);

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
            triggerRun.addObserver(triggerResult -> this.processTriggerResult(triggerResult, itemName, damageSource, actor, dropLocation));
            triggerRun.start();
        }

        SoundSequence soundSequence = properties.soundSequence();

        if (soundSequence != null) {
            SoundPlayback soundPlayback = soundEmitter.playSoundSequence(soundSequence, soundLocationSupplier);

            soundPlaybacks.add(soundPlayback);
        }
    }

    private void processTriggerResult(TriggerResult triggerResult, String itemName, DamageSource damageSource, ItemActor actor, Location dropLocation) {
        CollisionResult collisionResult = collisionResultAdapter.adapt(triggerResult);

        ItemEffectContext effectContext = new ItemEffectContext(itemName, collisionResult, damageSource, dropLocation, actor);
        itemEffect.startPerformance(effectContext);
    }
}
