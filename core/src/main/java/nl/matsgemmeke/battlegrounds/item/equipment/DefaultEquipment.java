package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import nl.matsgemmeke.battlegrounds.item.projectile.ProjectileProperties;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DefaultEquipment extends BaseWeapon implements Equipment {

    @Nullable
    private Activator activator;
    private Deployment deployment;
    @Nullable
    private EquipmentUser user;
    @Nullable
    private ItemTemplate displayItemTemplate;
    @Nullable
    private ProjectileProperties projectileProperties;

    @Nullable
    public Activator getActivator() {
        return activator;
    }

    public void setActivator(@Nullable Activator activator) {
        this.activator = activator;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    @Nullable
    public ItemTemplate getDisplayItemTemplate() {
        return displayItemTemplate;
    }

    public void setDisplayItemTemplate(@Nullable ItemTemplate displayItemTemplate) {
        this.displayItemTemplate = displayItemTemplate;
    }

    @Nullable
    public EquipmentUser getUser() {
        return user;
    }

    public void setUser(@Nullable EquipmentUser user) {
        this.user = user;
    }

    @Override
    public void activateDeployment(EquipmentUser user) {
        deployment.activate(user);
    }

    public boolean isActivatorReady() {
        return activator != null && activator.isReady();
    }

    public boolean isAwaitingDeployment() {
        return deployment.isPending();
    }

    public boolean isDeployed() {
        return deployment.isDeployed();
    }

    public boolean isMatching(ItemStack itemStack) {
        return displayItemTemplate != null && displayItemTemplate.matchesTemplate(itemStack)
                || activator != null && activator.isMatching(itemStack);
    }

    @Override
    public void performDeploymentAction(DeploymentAction deploymentAction, EquipmentUser user) {
        DestructionListener destructionListener = deployment::destroy;
        DeploymentResult result = deploymentAction.perform(user, destructionListener).orElse(null);

        if (result == null) {
            return;
        }

        deployment.processDeploymentResult(result);
    }

    @Override
    public void reset() {
        deployment.reset();
    }

    public boolean update() {
        if (displayItemTemplate == null) {
            return false;
        }

        Map<String, Object> values = this.getTemplateValues();
        itemStack = displayItemTemplate.createItemStack(values);
        return true;
    }

    private Map<String, Object> getTemplateValues() {
        Map<String, Object> values = new HashMap<>();

        if (name != null) {
            values.put("name", name);
        }

        return values;
    }
}
