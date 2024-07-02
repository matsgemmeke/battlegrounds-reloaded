package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultEquipment extends BaseWeapon implements Equipment {

    @Nullable
    private EquipmentHolder holder;
    @Nullable
    private Item droppedItem;
    @NotNull
    private ItemControls<EquipmentHolder> controls;

    public DefaultEquipment(@NotNull GameContext context) {
        super(context);
        this.controls = new ItemControls<>();
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

        World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("Cannot perform an item drop for a location without world");
        }

        droppedItem = world.dropItem(location, itemStack);

        context.registerItem(droppedItem);

        return droppedItem;
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
