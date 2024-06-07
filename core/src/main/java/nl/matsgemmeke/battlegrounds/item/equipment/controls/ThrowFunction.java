package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.game.audio.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.mechanism.EquipmentMechanism;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class ThrowFunction implements ItemFunction<EquipmentHolder> {

    // Take a high number to make sure the item cannot be picked up before its ignition
    private static final int DEFAULT_PICKUP_DELAY = 1000;

    @NotNull
    private AudioEmitter audioEmitter;
    private double projectileSpeed;
    @NotNull
    private EquipmentMechanism mechanism;
    @Nullable
    private ItemStack itemStack;
    @NotNull
    private Iterable<GameSound> sounds;

    public ThrowFunction(
            @NotNull EquipmentMechanism mechanism,
            @Nullable ItemStack itemStack,
            @NotNull AudioEmitter audioEmitter,
            double projectileSpeed
    ) {
        this.mechanism = mechanism;
        this.itemStack = itemStack;
        this.audioEmitter = audioEmitter;
        this.projectileSpeed = projectileSpeed;
        this.sounds = new HashSet<>();
    }

    public void addSounds(@NotNull Iterable<GameSound> sounds) {
        this.sounds = Iterables.concat(this.sounds, sounds);
    }

    public boolean isAvailable() {
        return true;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isPerforming() {
        return false;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull EquipmentHolder holder) {
        if (itemStack == null) {
            return false;
        }

        World world = holder.getEntity().getWorld();
        Location direction = holder.getThrowingDirection();

        Item item = world.dropItem(direction, itemStack);
        item.setPickupDelay(DEFAULT_PICKUP_DELAY);
        item.setVelocity(direction.getDirection().multiply(projectileSpeed));

        audioEmitter.playSounds(sounds, direction);

        // Prime the mechanism if it isn't already cooked by the holder
        if (!mechanism.isPrimed()) {
            mechanism.prime();
        }

        return true;
    }
}
