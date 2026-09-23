package nl.matsgemmeke.battlegrounds.arena.component.membership;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.membership.JoinResult;
import nl.matsgemmeke.battlegrounds.game.component.membership.MembershipService;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ArenaMembershipService implements MembershipService {

    private final Arena arena;
    private final PlayerRegistry playerRegistry;

    @Inject
    public ArenaMembershipService(Arena arena, PlayerRegistry playerRegistry) {
        this.arena = arena;
        this.playerRegistry = playerRegistry;
    }

    @Override
    public JoinResult join(Player player) {
        UUID playerId = player.getUniqueId();

        if (playerRegistry.isRegistered(playerId)) {
            return JoinResult.ALREADY_IN_GAME;
        }

        playerRegistry.register(player);

        arena.getLobbyLocation().ifPresent(player::teleport);

        return JoinResult.SUCCESS;
    }

    @Override
    public void leave(Player player) {

    }
}
