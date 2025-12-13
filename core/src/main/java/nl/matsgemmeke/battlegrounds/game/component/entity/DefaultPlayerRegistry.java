package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
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

    @Override
    public Optional<GamePlayer> findByUniqueId(UUID uuid) {
        return playerContainer.getEntity(uuid);
    }

    @Override
    public Collection<GamePlayer> getAll() {
        return playerContainer.getEntities();
    }

    @Override
    public boolean isRegistered(UUID uniqueId) {
        return playerContainer.getEntity(uniqueId).isPresent();
    }

    @Override
    public void deregister(UUID uniqueId) {
        playerContainer.removeEntity(uniqueId);
    }

    @Override
    public GamePlayer register(Player player) {
        HitboxProvider<Player> hitboxProvider = hitboxResolver.resolveHitboxProvider(player);

        UUID uniqueId = player.getUniqueId();
        gameContextProvider.registerEntity(uniqueId, gameKey);

        GamePlayer gamePlayer = gamePlayerFactory.create(player, hitboxProvider);
        playerContainer.addEntity(gamePlayer);

        return gamePlayer;
    }
}
