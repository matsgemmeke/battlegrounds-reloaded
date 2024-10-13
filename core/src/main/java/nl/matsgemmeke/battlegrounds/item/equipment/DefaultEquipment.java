package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultEquipment extends BaseWeapon implements Equipment {

    @Nullable
    private EquipmentHolder holder;
    @NotNull
    private ItemControls<EquipmentHolder> controls;
    @Nullable
    private ItemStack activatorItemStack;
    @NotNull
    private List<Deployable> deployedObjects;
    @Nullable
    private TextTemplate displayNameTemplate;

    public DefaultEquipment() {
        this.controls = new ItemControls<>();
        this.deployedObjects = new ArrayList<>();
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

    @NotNull
    public List<Deployable> getDeployedObjects() {
        return deployedObjects;
    }

    @Nullable
    public TextTemplate getDisplayNameTemplate() {
        return displayNameTemplate;
    }

    public void setDisplayNameTemplate(@Nullable TextTemplate displayNameTemplate) {
        this.displayNameTemplate = displayNameTemplate;
    }

    @Nullable
    public EquipmentHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable EquipmentHolder holder) {
        this.holder = holder;
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
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (displayNameTemplate != null) {
            Map<String, Object> values = this.getTemplateValues();
            String displayName = displayNameTemplate.replace(values);

            itemMeta.setDisplayName(displayName);
        }

        itemStack.setItemMeta(itemMeta);

        if (holder != null) {
            holder.setHeldItem(itemStack);
        }

        return true;
    }

    @NotNull
    private Map<String, Object> getTemplateValues() {
        return Map.of("name", name);
    }
}
