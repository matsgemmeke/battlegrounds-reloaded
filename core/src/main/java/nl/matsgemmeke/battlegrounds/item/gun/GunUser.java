package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.ItemUser;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import nl.matsgemmeke.battlegrounds.item.shoot.ShotPerformer;

/**
 * An entity that is capable of owning and operating a {@link Gun}.
 */
public interface GunUser extends ItemUser, ReloadPerformer, ScopeUser, ShotPerformer {
}
