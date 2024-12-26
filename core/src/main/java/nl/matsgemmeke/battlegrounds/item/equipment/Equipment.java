package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.activation.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.projectile.ProjectileProperties;
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
     * Gets the deployment properties associated with this equipment item. Returns null if no properties are set.
     *
     * @return the deployment properties of the equipment item or null if not set
     */
    @Nullable
    DeploymentProperties getDeploymentProperties();

    /**
     * Sets the deployment properties associated with this equipment item.
     *
     * @param deploymentProperties the deployment properties of the equipment item
     */
    void setDeploymentProperties(@Nullable DeploymentProperties deploymentProperties);

    /**
     * Gets the effect activation system associated with the equipment.
     *
     * @return the equipment's effect activation
     */
    @Nullable
    ItemEffectActivation getEffectActivation();

    /**
     * Sets the effect activation system associated with the equipment.
     *
     * @param effectActivation the equipment's effect activation
     *
     */
    void setEffectActivation(@Nullable ItemEffectActivation effectActivation);

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
}
