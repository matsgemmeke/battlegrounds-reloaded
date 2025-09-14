package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;

public interface MagazineReloadSystemFactory {

    ReloadSystem create(ReloadProperties properties, AmmunitionStorage ammunitionStorage);
}
