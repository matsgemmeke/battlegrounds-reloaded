package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.deployment.DeployableSource;
import nl.matsgemmeke.battlegrounds.item.deployment.DroppedItem;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ThrowFunction implements ItemFunction<EquipmentHolder> {

    // Take a high number to make sure the item cannot be picked up before its ignition
    private static final int DEFAULT_PICKUP_DELAY = 100000;

    @NotNull
    private AudioEmitter audioEmitter;
    private boolean performing;
    @NotNull
    private DeployableSource item;
    private double projectileSpeed;
    @NotNull
    private ItemMechanismActivation mechanismActivation;
    @NotNull
    private ItemStack itemStack;
    @NotNull
    private Iterable<GameSound> sounds;
    private long delayAfterThrow;
    @NotNull
    private TaskRunner taskRunner;

    public ThrowFunction(
            @NotNull DeployableSource item,
            @NotNull ItemStack itemStack,
            @NotNull ItemMechanismActivation mechanismActivation,
            @NotNull AudioEmitter audioEmitter,
            @NotNull TaskRunner taskRunner,
            double projectileSpeed,
            long delayAfterThrow
    ) {
        this.item = item;
        this.itemStack = itemStack;
        this.mechanismActivation = mechanismActivation;
        this.audioEmitter = audioEmitter;
        this.taskRunner = taskRunner;
        this.projectileSpeed = projectileSpeed;
        this.delayAfterThrow = delayAfterThrow;
        this.performing = false;
        this.sounds = new HashSet<>();
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
        if (!item.getDeployedObjects().isEmpty()) {
            return false;
        }

        Location location = holder.getLocation();
        World world = holder.getWorld();
        Location throwingDirection = holder.getThrowingDirection();
        Vector velocity = throwingDirection.getDirection().multiply(projectileSpeed);

        Item itemEntity = world.dropItem(throwingDirection, itemStack);
        itemEntity.setPickupDelay(DEFAULT_PICKUP_DELAY);
        itemEntity.setVelocity(velocity);

        DroppedItem droppedItem = new DroppedItem(itemEntity);
        item.onDeploy(droppedItem);

        audioEmitter.playSounds(sounds, location);

        performing = true;

        taskRunner.runTaskLater(() -> performing = false, delayAfterThrow);

        // Check if the activation mechanism is priming its next deployment. If yes, assign the dropped item. Otherwise,
        // deploy like normally.
        if (mechanismActivation.isPriming()) {
            mechanismActivation.onDeployDeferredObject(droppedItem);
        } else {
            mechanismActivation.prime(holder, droppedItem);
        }

        return true;
    }
}
