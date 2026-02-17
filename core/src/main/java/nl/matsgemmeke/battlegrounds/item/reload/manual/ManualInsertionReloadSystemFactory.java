package nl.matsgemmeke.battlegrounds.item.reload.manual;

import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;

public interface ManualInsertionReloadSystemFactory {

    ReloadSystem create(ReloadProperties properties, ResourceContainer resourceContainer);
}
