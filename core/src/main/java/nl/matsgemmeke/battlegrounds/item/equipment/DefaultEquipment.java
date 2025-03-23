package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import nl.matsgemmeke.battlegrounds.item.projectile.ProjectileProperties;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.World;
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
    @NotNull
    private DeploymentHandler deploymentHandler;
    private DeploymentProperties deploymentProperties;
    @Nullable
    private EquipmentHolder holder;
    @NotNull
    private ItemControls<EquipmentHolder> controls;
    @Nullable
    private ItemEffect effect;
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

    public void setControls(@NotNull ItemControls<EquipmentHolder> controls) {
        this.controls = controls;
    }

    @NotNull
    public DeploymentHandler getDeploymentHandler() {
        return deploymentHandler;
    }

    public void setDeploymentHandler(@NotNull DeploymentHandler deploymentHandler) {
        this.deploymentHandler = deploymentHandler;
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
    public ItemEffect getEffect() {
        return effect;
    }

    public void setEffect(@Nullable ItemEffect effect) {
        this.effect = effect;
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

    public boolean isAwaitingDeployment() {
        return deploymentHandler.isAwaitingDeployment();
    }

    public boolean isMatching(@NotNull ItemStack itemStack) {
        return itemTemplate != null && itemTemplate.matchesTemplate(itemStack)
                || activator != null && activator.isMatching(itemStack);
    }

    public boolean isPerformingDeployment() {
        return deploymentHandler.isPerforming();
    }

    public void onChangeFrom() {
        controls.cancelAllFunctions();
    }

    public void onChangeTo() {
    }

    public void onDeployDeploymentObject(@NotNull DeploymentObject deploymentObject) {
        deploymentObjects.add(deploymentObject);
    }

    public void onDestroyDeploymentObject(@NotNull DeploymentObject deploymentObject) {
        if (effect != null) {
            // Activate the effect if it's configured to do so and the item has a holder for the activation
            if (deploymentProperties != null
                    && deploymentProperties.isActivatedOnDestroy()
                    && deploymentObject.getLastDamage() != null
                    && deploymentObject.getLastDamage().type() != DamageType.ENVIRONMENTAL_DAMAGE) {
                effect.activateInstantly();
            }

            if (deploymentProperties != null && deploymentProperties.isResetOnDestroy()) {
                effect.reset();
            }

            effect.cancelActivation();
        }

        if (deploymentProperties != null && deploymentProperties.getDestroyParticleEffect() != null) {
            ParticleEffect particleEffect = deploymentProperties.getDestroyParticleEffect();
            World world = deploymentObject.getWorld();
            Location location = deploymentObject.getLocation();

            ParticleEffectSpawner particleEffectSpawner = new ParticleEffectSpawner();
            particleEffectSpawner.spawnParticleEffect(particleEffect, world, location);
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

    public void performDeployment(@NotNull Deployment deployment, @NotNull EquipmentHolder holder) {
        deploymentHandler.handleDeployment(deployment, holder, holder.getEntity());
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
