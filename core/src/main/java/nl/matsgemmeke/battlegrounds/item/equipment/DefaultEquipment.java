package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.projectile.ProjectileProperties;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEquipment extends BaseWeapon implements Equipment {

    @Nullable
    private Activator activator;
    @Nullable
    private DeploymentProperties deploymentProperties;
    @Nullable
    private EquipmentHolder holder;
    @NotNull
    private ItemControls<EquipmentHolder> controls;
    @Nullable
    private ItemEffectActivation effectActivation;
    @Nullable
    private ItemTemplate itemTemplate;
    @Nullable
    private ItemTemplate throwItemTemplate;
    @NotNull
    private List<DeploymentObject> deploymentObjects;
    @Nullable
    private ProjectileProperties projectileProperties;

    public DefaultEquipment() {
        this.controls = new ItemControls<>();
        this.deploymentObjects = new ArrayList<>();
    }

    @Nullable
    public Activator getActivator() {
        return activator;
    }

    public void setActivator(@Nullable Activator activator) {
        this.activator = activator;
    }

    @NotNull
    public ItemControls<EquipmentHolder> getControls() {
        return controls;
    }

    @NotNull
    public List<DeploymentObject> getDeploymentObjects() {
        return deploymentObjects;
    }

    @Nullable
    public DeploymentProperties getDeploymentProperties() {
        return deploymentProperties;
    }

    public void setDeploymentProperties(@Nullable DeploymentProperties deploymentProperties) {
        this.deploymentProperties = deploymentProperties;
    }

    @Nullable
    public ItemEffectActivation getEffectActivation() {
        return effectActivation;
    }

    public void setEffectActivation(@Nullable ItemEffectActivation effectActivation) {
        this.effectActivation = effectActivation;
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

    @Nullable
    public ProjectileProperties getProjectileProperties() {
        return projectileProperties;
    }

    public void setProjectileProperties(@Nullable ProjectileProperties projectileProperties) {
        this.projectileProperties = projectileProperties;
    }

    @Nullable
    public ItemTemplate getThrowItemTemplate() {
        return throwItemTemplate;
    }

    public void setThrowItemTemplate(@Nullable ItemTemplate throwItemTemplate) {
        this.throwItemTemplate = throwItemTemplate;
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return itemTemplate != null && itemTemplate.matchesTemplate(itemStack)
                || activator != null && activator.isMatching(itemStack);
    }

    public void onChangeFrom() {
    }

    public void onChangeTo() {
    }

    public void onDeployDeploymentObject(@NotNull DeploymentObject deploymentObject) {
        deploymentObjects.add(deploymentObject);
    }

    public void onDestroyDeploymentObject(@NotNull DeploymentObject deploymentObject) {
        // Activate the effect if it's configured to do so and the item has a holder for the activation
        if (effectActivation != null
                && holder != null
                && deploymentProperties != null
                && deploymentProperties.isActivatedOnDestroy()) {
            effectActivation.activateInstantly(holder);
        }

        deploymentObjects.remove(deploymentObject);
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
