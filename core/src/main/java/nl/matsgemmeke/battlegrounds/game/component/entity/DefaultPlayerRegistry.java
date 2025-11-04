package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.resolver.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.EntityContainer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class DefaultPlayerRegistry implements PlayerRegistry {

    private final DefaultGamePlayerFactory gamePlayerFactory;
    private final EntityContainer<GamePlayer> playerContainer;
    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final HitboxResolver hitboxResolver;

    @Inject
    public DefaultPlayerRegistry(DefaultGamePlayerFactory gamePlayerFactory, GameContextProvider gameContextProvider, GameKey gameKey, HitboxResolver hitboxResolver) {
        this.gamePlayerFactory = gamePlayerFactory;
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.hitboxResolver = hitboxResolver;
        this.playerContainer = new EntityContainer<>();
    }

    public Optional<GamePlayer> findByEntity(Player player) {
        for (GamePlayer gamePlayer : playerContainer.getEntities()) {
            if (gamePlayer.getEntity() == player) {
                return Optional.of(gamePlayer);
            }
        }

        return Optional.empty();
    }

    public Optional<GamePlayer> findByUniqueId(UUID uuid) {
        return playerContainer.getEntity(uuid);
    }

    public Collection<GamePlayer> getAll() {
        return playerContainer.getEntities();
    }

    public boolean isRegistered(Player player) {
        return playerContainer.getEntity(player).isPresent();
    }

    public boolean isRegistered(UUID uniqueId) {
        return playerContainer.getEntity(uniqueId).isPresent();
    }

    public void deregister(UUID uniqueId) {
        playerContainer.removeEntity(uniqueId);
    }

    public GamePlayer register(Player player) {
        Hitbox hitbox = hitboxResolver.resolveHitbox(player).orElse(null);

        UUID playerId = player.getUniqueId();
        gameContextProvider.registerEntity(playerId, gameKey);

        GamePlayer gamePlayer = gamePlayerFactory.create(player, hitbox);
        playerContainer.addEntity(gamePlayer);

        return gamePlayer;
    }
}
