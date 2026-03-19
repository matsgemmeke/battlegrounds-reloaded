package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

import java.util.HashMap;
import java.util.Map;

public class EquipmentSpec {

    @Required
    public String name;

    public String description;

    @Required
    public String equipmentType;

    @Required
    @Valid
    public EquipmentItemsSpec items;

    @Required
    @Valid
    public ControlsSpec controls;

    @Required
    @Valid
    public DeploymentSpec deploy;

    @Required
    @Valid
    public ItemEffectSpec effect;

    public Map<String, @Valid ProjectileEffectSpec> projectileEffects = new HashMap<>();
}
