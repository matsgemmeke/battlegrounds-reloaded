package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
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
    @NotNull
    private ItemEffectActivation effectActivation;
    @NotNull
    private ItemTemplate itemTemplate;
    @NotNull
    private Set<ProjectileProperty> projectileProperties;
    @NotNull
    private TaskRunner taskRunner;
    @NotNull
    private ThrowProperties properties;

    public ThrowFunction(
            @NotNull ThrowProperties properties,
            @NotNull ItemTemplate itemTemplate,
            @NotNull ItemEffectActivation effectActivation,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner
    ) {
        this.properties = properties;
        this.itemTemplate = itemTemplate;
        this.effectActivation = effectActivation;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.performing = false;
        this.projectileProperties = new HashSet<>();
    }

    public void addProjectileProperties(@NotNull ProjectileProperty projectileProperty) {
        projectileProperties.add(projectileProperty);
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
        Vector velocity = throwingDirection.getDirection().multiply(properties.velocity());

        ItemStack itemStack = itemTemplate.createItemStack();

        Item itemEntity = world.dropItem(throwingDirection, itemStack);
        itemEntity.setPickupDelay(DEFAULT_PICKUP_DELAY);
        itemEntity.setVelocity(velocity);

        audioEmitter.playSounds(properties.throwSounds(), location);

        performing = true;

        taskRunner.runTaskLater(() -> performing = false, properties.delayAfterThrow());

        DroppedItem droppedItem = new DroppedItem(itemEntity);

        effectActivation.prime(holder, droppedItem);
        projectileProperties.forEach(property -> property.onLaunch(droppedItem));
        return true;
    }
}
