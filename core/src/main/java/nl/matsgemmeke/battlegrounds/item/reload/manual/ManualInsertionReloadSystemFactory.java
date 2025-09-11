package nl.matsgemmeke.battlegrounds.item.reload.manual;

import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;

public interface ManualInsertionReloadSystemFactory {

    ReloadSystem create(ReloadProperties properties, AmmunitionStorage ammunitionStorage);
}
