package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class MeleeWeaponSpec {

    @Required
    public String name;

    public String description;

    public ControlsSpec controls;

    @Required
    public DamageSpec damage;

    @Required
    public MeleeWeaponItemSpec items;
}
