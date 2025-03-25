package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.Damageable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface DeploymentObject extends ItemEffectSource, Damageable {

    boolean matchesEntity(@NotNull Entity entity);
}
