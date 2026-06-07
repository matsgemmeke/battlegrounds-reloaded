package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.EntityKeyRegistry;
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
    private final EntityKeyRegistry entityKeyRegistry;
    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final HitboxResolver hitboxResolver;

    @Inject
    public DefaultPlayerRegistry(
            DefaultGamePlayerFactory gamePlayerFactory,
            EntityKeyRegistry entityKeyRegistry,
            GameContextProvider gameContextProvider,
            GameKey gameKey,
            HitboxResolver hitboxResolver
    ) {
        this.gamePlayerFactory = gamePlayerFactory;
        this.entityKeyRegistry = entityKeyRegistry;
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
        UUID playerId = player.getUniqueId();

        gameContextProvider.registerEntity(playerId, gameKey);

        GamePlayer gamePlayer = gamePlayerFactory.create(player, hitboxProvider);
        playerContainer.addEntity(gamePlayer);

        EntityKey entityKey = EntityKey.fromEntityType(player.getType());
        entityKeyRegistry.register(playerId, entityKey);

        return gamePlayer;
    }
}
