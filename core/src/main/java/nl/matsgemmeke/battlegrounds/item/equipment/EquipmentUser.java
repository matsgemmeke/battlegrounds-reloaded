package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemUser;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.entity.Entity;

public interface EquipmentUser extends ItemUser, Deployer {

    @Deprecated
    Entity getEntity();
}
