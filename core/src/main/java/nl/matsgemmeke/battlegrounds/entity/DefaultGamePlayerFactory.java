package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import org.bukkit.entity.Player;

public interface DefaultGamePlayerFactory {

    GamePlayer create(Player player, Hitbox hitbox);
}
