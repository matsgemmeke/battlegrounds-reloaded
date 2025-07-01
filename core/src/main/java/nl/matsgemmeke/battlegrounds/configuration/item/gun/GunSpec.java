package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class GunSpec {

    @Required
    public String id;
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
    public AmmoSpec ammo;
    @Required
    public ControlsSpec controls;
    @Required
    public ItemSpec item;
    @Required
    public ReloadingSpec reloading;
    public ScopeSpec scope;
    @Required
    public ShootingSpec shooting;
}
