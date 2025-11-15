package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import org.bukkit.entity.Player;

public interface DefaultGamePlayerFactory {

    GamePlayer create(Player player, HitboxProvider hitboxProvider);
}
