package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ReloadingSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class MeleeWeaponSpec {

    @Required
    public String name;

    public String description;

    @Valid
    public ControlsSpec controls;

    @Required
    @Valid
    public AmmoSpec ammo;

    @Required
    @Valid
    public DamageSpec damage;

    @Required
    @Valid
    public ItemSpec item;

    @Valid
    public ReloadingSpec reloading;

    @Valid
    public ThrowingSpec throwing;
}
