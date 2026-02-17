package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.entity.Entity;

public interface EquipmentHolder extends ItemHolder, Deployer {

    @Deprecated
    Entity getEntity();
}
