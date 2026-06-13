package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.Resettable;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.deploy.action.DeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import org.jetbrains.annotations.Nullable;

public interface Equipment extends Weapon, Resettable {

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
     * Gets the user of the equipmemt item. Returns null if the equipment does not have a user.
     *
     * @return the equipment user or null if it does not have one
     */
    @Nullable
    EquipmentUser getUser();

    /**
     * Sets the user of the equipment item.
     *
     * @param user the equipment user
     */
    void setUser(@Nullable EquipmentUser user);

    /**
     * Immediately activates the deployment object in the current deployment process. This method does not have any
     * effects if no deployments have taken place before invoking.
     *
     * @param user the user that activates the deployment
     */
    void activateDeployment(EquipmentUser user);

    /**
     * Gets whether the equipment's activator is ready for use. Returns {@code true} if the equipment has an activator
     * and is ready to be triggered, otherwise {@code false}.
     *
     * @return whether the activator item is ready for use
     */
    boolean isActivatorReady();

    boolean isAwaitingDeployment();

    boolean isDeployed();

    /**
     * Performs a deployment action on the equipment item.
     *
     * @param deploymentAction the deployment action
     * @param user             the user that performs the deployment action
     */
    void performDeploymentAction(DeploymentAction deploymentAction, EquipmentUser user);
}
