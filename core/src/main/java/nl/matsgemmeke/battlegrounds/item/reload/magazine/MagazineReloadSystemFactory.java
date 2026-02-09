package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;

public interface MagazineReloadSystemFactory {

    ReloadSystem create(ReloadProperties properties, ResourceContainer resourceContainer);
}
