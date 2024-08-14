package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultEquipment extends BaseWeapon implements Equipment {

    @NotNull
    private EntityRegistry<GameItem, Item> itemRegistry;
    @Nullable
    private EquipmentHolder holder;
    @Nullable
    private Item droppedItem;
    @NotNull
    private ItemControls<EquipmentHolder> controls;
    @Nullable
    private ItemStack activatorItemStack;

    public DefaultEquipment(@NotNull EntityRegistry<GameItem, Item> itemRegistry) {
        this.itemRegistry = itemRegistry;
        this.controls = new ItemControls<>();
    }

    @Nullable
    public ItemStack getActivatorItemStack() {
        return activatorItemStack;
    }

    public void setActivatorItemStack(@Nullable ItemStack activatorItemStack) {
        this.activatorItemStack = activatorItemStack;
    }

    @NotNull
    public ItemControls<EquipmentHolder> getControls() {
        return controls;
    }

    @Nullable
    public Item getDroppedItem() {
        return droppedItem;
    }

    @Nullable
    public EquipmentHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable EquipmentHolder holder) {
        this.holder = holder;
    }

    public boolean canDrop() {
        return itemStack != null;
    }

    @NotNull
    public Item dropItem(@NotNull Location location) {
        if (itemStack == null) {
            throw new IllegalStateException("Cannot perform an item drop for an item without item stack");
        }

        if (holder == null) {
            throw new IllegalStateException("Cannot perform an item drop if there is no holder");
        }

        World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Cannot perform an item drop for a location without world");
        }

        droppedItem = world.dropItem(location, itemStack);

        itemRegistry.registerEntity(droppedItem);

        // Update the original item to the activator item. If the activator item is null it will set an empty item.
        holder.setHeldItem(activatorItemStack);

        return droppedItem;
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return super.isMatching(itemStack) || activatorItemStack != null && activatorItemStack.isSimilar(itemStack);
    }

    public void onChangeFrom() {
    }

    public void onChangeTo() {
    }

    public void onDrop() {
    }

    public void onLeftClick() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.LEFT_CLICK, holder);
    }

    public void onPickUp(@NotNull EquipmentHolder holder) {
    }

    public void onRightClick() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.RIGHT_CLICK, holder);
    }

    public void onSwapFrom() {
    }

    public void onSwapTo() {
    }

    public boolean update() {
        return false;
    }
}
