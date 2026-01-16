package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public record CheckResult(@Nullable DamageTarget hitTarget, @Nullable Block hitBlock, @Nullable Location hitLocation) {
}
