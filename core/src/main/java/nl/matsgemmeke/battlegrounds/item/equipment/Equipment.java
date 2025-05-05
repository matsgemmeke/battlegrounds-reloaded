package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.projectile.ProjectileProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Equipment extends Weapon, DeployableItem, Interactable<EquipmentHolder> {

    /**
     * Gets the activator item used to activate the equipment. Returns null if the equipment does not utilize an
     * activator.
     *
     * @return the equipment's activator item or null if it does not have one
     */
    @Nullable
    Activator getActivator();

    /**
     * Sets the activator item used to activate the equipment.
     *
     * @param activator the equipment's activator item
     */
    void setActivator(@Nullable Activator activator);

    /**
     * Gets the holder of the equipmemt item. Returns null if the equipment does not have a holder.
     *
     * @return the equipment holder or null if it does not have one
     */
    @Nullable
    EquipmentHolder getHolder();

    /**
     * Sets the holder of the equipment item.
     *
     * @param holder the equipment holder
     */
    void setHolder(@Nullable EquipmentHolder holder);

    /**
     * Gets the projectile properties associated with this equipment item. Returns null if no properties are set.
     *
     * @return the projectile properties of the equipment item or null if not set
     */
    @Nullable
    ProjectileProperties getProjectileProperties();

    /**
     * Sets the projectile properties associated with this equipment item.
     *
     * @param projectileProperties the projectile properties of the equipment item
     */
    void setProjectileProperties(@Nullable ProjectileProperties projectileProperties);

    /**
     * Gets the item template to be used for throwing the equipment. Returns null if no template is set for throwing.
     *
     * @return the item template for throwing the equipment or null if not set
     */
    @Nullable
    ItemTemplate getThrowItemTemplate();

    /**
     * Sets the item template to be used for throwing the equipment.
     *
     * @param itemTemplate the item template for throwing the equipment
     */
    void setThrowItemTemplate(@Nullable ItemTemplate itemTemplate);

    /**
     * Immediately activates the deployment object in the current deployment process. This method does not have any
     * effects if no deployments have taken place before invoking.
     *
     * @param holder the holder that activates the deployment
     */
    void activateDeployment(@NotNull EquipmentHolder holder);

    /**
     * Retrieves the deployment object associated with the equipment item if it has been deployed.
     *
     * @return the deployment object if deployed, or {@code null} if not deployed
     */
    @Nullable
    DeploymentObject getDeploymentObject();

    /**
     * Gets whether the equipment's activator is ready for use. Returns {@code true} if the equipment has an activator
     * and is ready to be triggered, otherwise {code false}.
     *
     * @return whether the activator item is ready for use
     */
    boolean isActivatorReady();

    boolean isAwaitingDeployment();

    boolean isDeployed();

    /**
     * Performs a deployment on the equipment item.
     *
     * @param deployment the deployment instance
     * @param holder the holder that performs the deployment
     */
    void performDeployment(@NotNull Deployment deployment, @NotNull EquipmentHolder holder);
}
