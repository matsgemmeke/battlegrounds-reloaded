package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.DroppedItem;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.projectile.ProjectileProperty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ThrowFunction implements ItemFunction<EquipmentHolder> {

    // Take a high number to make sure the item cannot be picked up before its ignition
    private static final int DEFAULT_PICKUP_DELAY = 100000;

    @NotNull
    private AudioEmitter audioEmitter;
    private boolean performing;
    private double projectileSpeed;
    @NotNull
    private ItemEffectActivation effectActivation;
    @NotNull
    private ItemTemplate itemTemplate;
    @NotNull
    private Iterable<GameSound> sounds;
    private long delayAfterThrow;
    @NotNull
    private Set<ProjectileProperty> projectileProperties;
    @NotNull
    private TaskRunner taskRunner;

    public ThrowFunction(
            @NotNull ItemTemplate itemTemplate,
            @NotNull ItemEffectActivation effectActivation,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            double projectileSpeed,
            long delayAfterThrow
    ) {
        this.itemTemplate = itemTemplate;
        this.effectActivation = effectActivation;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.projectileSpeed = projectileSpeed;
        this.delayAfterThrow = delayAfterThrow;
        this.performing = false;
        this.projectileProperties = new HashSet<>();
        this.sounds = new HashSet<>();
    }

    public void addProjectileProperties(@NotNull ProjectileProperty projectileProperty) {
        projectileProperties.add(projectileProperty);
    }

    public void addSounds(@NotNull Iterable<GameSound> sounds) {
        this.sounds = Iterables.concat(this.sounds, sounds);
    }

    public boolean isAvailable() {
        return true;
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return performing;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull EquipmentHolder holder) {
        Location location = holder.getLocation();
        World world = holder.getWorld();
        Location throwingDirection = holder.getThrowingDirection();
        Vector velocity = throwingDirection.getDirection().multiply(projectileSpeed);

        ItemStack itemStack = itemTemplate.createItemStack();

        Item itemEntity = world.dropItem(throwingDirection, itemStack);
        itemEntity.setPickupDelay(DEFAULT_PICKUP_DELAY);
        itemEntity.setVelocity(velocity);

        audioEmitter.playSounds(sounds, location);

        performing = true;

        taskRunner.runTaskLater(() -> performing = false, delayAfterThrow);

        DroppedItem droppedItem = new DroppedItem(itemEntity);

        effectActivation.prime(holder, droppedItem);
        projectileProperties.forEach(property -> property.onLaunch(droppedItem));
        return true;
    }
}
