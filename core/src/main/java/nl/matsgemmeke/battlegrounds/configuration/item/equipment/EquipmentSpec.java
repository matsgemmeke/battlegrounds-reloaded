package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.HashMap;
import java.util.Map;

public class EquipmentSpec {

    @Required
    public String name;
    public String description;
    @Required
    public String equipmentType;
    @Required
    public EquipmentItemsSpec items;
    @Required
    public ControlsSpec controls;
    @Required
    public DeploymentSpec deploy;
    @Required
    public ItemEffectSpec effect;
    public Map<String, ProjectileEffectSpec> projectileEffects = new HashMap<>();
}
