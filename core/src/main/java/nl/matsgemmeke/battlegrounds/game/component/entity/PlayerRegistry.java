package nl.matsgemmeke.battlegrounds.game.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRegistry {

    Optional<GamePlayer> findByUniqueId(UUID uniqueId);

    Collection<GamePlayer> getAll();

    boolean isRegistered(UUID uniqueId);

    void deregister(UUID uniqueId);

    GamePlayer register(Player player);
}
