package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class GunSpec {

    @Required
    public String name;

    public String description;

    @Required
    public String gunType;

    @Required
    public Integer levelUnlocked;

    @Required
    public Integer price;

    @Required
    @Valid
    public AmmoSpec ammo;

    @Required
    @Valid
    public ControlsSpec controls;

    @Required
    @Valid
    public ItemSpec item;

    @Required
    @Valid
    public ReloadingSpec reloading;

    @Valid
    public ScopeSpec scope;

    @Required
    @Valid
    public ShootingSpec shooting;
}
