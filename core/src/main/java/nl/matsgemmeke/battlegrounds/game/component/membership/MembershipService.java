package nl.matsgemmeke.battlegrounds.game.component.membership;

import org.bukkit.entity.Player;

public interface MembershipService {

    JoinResult join(Player player);

    void leave(Player player);
}
