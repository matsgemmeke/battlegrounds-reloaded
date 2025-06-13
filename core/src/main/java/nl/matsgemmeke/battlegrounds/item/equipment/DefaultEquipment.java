package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
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
    private DeploymentHandler deploymentHandler;
    @Nullable
    private EquipmentHolder holder;
    @NotNull
    private ItemControls<EquipmentHolder> controls;
    @Nullable
    private ItemTemplate displayItemTemplate;
    @Nullable
    private ItemTemplate throwItemTemplate;
    @NotNull
    private List<DeploymentObject> deploymentObjects;
    @Nullable
    private ProjectileProperties projectileProperties;

    public DefaultEquipment(@NotNull String id) {
        super(id);
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

    public void setControls(@NotNull ItemControls<EquipmentHolder> controls) {
        this.controls = controls;
    }

    public DeploymentHandler getDeploymentHandler() {
        return deploymentHandler;
    }

    public void setDeploymentHandler(DeploymentHandler deploymentHandler) {
        this.deploymentHandler = deploymentHandler;
    }

    @NotNull
    public List<DeploymentObject> getDeploymentObjects() {
        return deploymentObjects;
    }

    @Nullable
    public EquipmentHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable EquipmentHolder holder) {
        this.holder = holder;
    }

    @Nullable
    public ItemTemplate getDisplayItemTemplate() {
        return displayItemTemplate;
    }

    public void setDisplayItemTemplate(@Nullable ItemTemplate displayItemTemplate) {
        this.displayItemTemplate = displayItemTemplate;
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

    public void activateDeployment(@NotNull EquipmentHolder holder) {
        deploymentHandler.activateDeployment(holder, holder.getEntity());
    }

    public void cleanup() {
        deploymentHandler.cleanupDeployment();
    }

    public void destroyDeployment() {
        deploymentHandler.destroyDeployment();
    }

    @Nullable
    public DeploymentObject getDeploymentObject() {
        return deploymentHandler.getDeploymentObject();
    }

    public boolean isActivatorReady() {
        return activator != null && activator.isReady();
    }

    public boolean isAwaitingDeployment() {
        return deploymentHandler.isAwaitingDeployment();
    }

    public boolean isDeployed() {
        return deploymentHandler.isDeployed();
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return displayItemTemplate != null && displayItemTemplate.matchesTemplate(itemStack)
                || activator != null && activator.isMatching(itemStack);
    }

    public void onChangeFrom() {
        controls.cancelAllFunctions();
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

    public void performDeployment(@NotNull Deployment deployment, @NotNull EquipmentHolder holder) {
        deploymentHandler.handleDeployment(deployment, holder, holder.getEntity());
    }

    public boolean update() {
        if (displayItemTemplate == null) {
            return false;
        }

        Map<String, Object> values = this.getTemplateValues();
        itemStack = displayItemTemplate.createItemStack(values);
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
