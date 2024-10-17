package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEquipment extends BaseWeapon implements Equipment {

    @Nullable
    private EquipmentHolder holder;
    @NotNull
    private ItemControls<EquipmentHolder> controls;
    @Nullable
    private ItemStack activatorItemStack;
    @Nullable
    private ItemTemplate activatorItemTemplate;
    @Nullable
    private ItemTemplate itemTemplate;
    @NotNull
    private List<Deployable> deployedObjects;

    public DefaultEquipment() {
        this.controls = new ItemControls<>();
        this.deployedObjects = new ArrayList<>();
    }

    @Nullable
    public ItemTemplate getActivatorItemTemplate() {
        return activatorItemTemplate;
    }

    public void setActivatorItemTemplate(@Nullable ItemTemplate activatorItemTemplate) {
        this.activatorItemTemplate = activatorItemTemplate;
    }

    @NotNull
    public ItemControls<EquipmentHolder> getControls() {
        return controls;
    }

    @NotNull
    public List<Deployable> getDeployedObjects() {
        return deployedObjects;
    }

    @Nullable
    public EquipmentHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable EquipmentHolder holder) {
        this.holder = holder;
    }

    @Nullable
    public ItemTemplate getItemTemplate() {
        return itemTemplate;
    }

    public void setItemTemplate(@Nullable ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return super.isMatching(itemStack) || activatorItemStack != null && activatorItemStack.isSimilar(itemStack);
    }

    public void onChangeFrom() {
    }

    public void onChangeTo() {
    }

    public void onDeploy(@NotNull Deployable object) {
        if (holder == null) {
            return;
        }

        deployedObjects.add(object);

        if (activatorItemTemplate != null) {
            Map<String, Object> values = this.getTemplateValues();
            activatorItemStack = activatorItemTemplate.createItemStack(values);
        }

        // Update the original item to the activator item. If the activator item is null it will set an empty item.
        holder.setHeldItem(activatorItemStack);
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
        if (itemTemplate == null) {
            return false;
        }

        Map<String, Object> values = this.getTemplateValues();
        itemStack = itemTemplate.createItemStack(values);

        if (holder != null) {
            holder.setHeldItem(itemStack);
        }

        return true;
    }

    @NotNull
    private Map<String, Object> getTemplateValues() {
        Map<String, Object> values = new HashMap<>();

        if (name != null) {
            values.put("name", name);
        }

        return values;
    }
}
