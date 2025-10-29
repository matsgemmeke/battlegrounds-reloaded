package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.PlayerHitbox;
import nl.matsgemmeke.battlegrounds.game.EntityContainer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class DefaultPlayerRegistry implements PlayerRegistry {

    @NotNull
    private final DefaultGamePlayerFactory gamePlayerFactory;
    @NotNull
    private final EntityContainer<GamePlayer> playerContainer;
    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameKey gameKey;

    @Inject
    public DefaultPlayerRegistry(@NotNull DefaultGamePlayerFactory gamePlayerFactory, @NotNull GameContextProvider gameContextProvider, @NotNull GameKey gameKey) {
        this.gamePlayerFactory = gamePlayerFactory;
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.playerContainer = new EntityContainer<>();
    }

    public Optional<GamePlayer> findByEntity(@NotNull Player player) {
        for (GamePlayer gamePlayer : playerContainer.getEntities()) {
            if (gamePlayer.getEntity() == player) {
                return Optional.of(gamePlayer);
            }
        }

        return Optional.empty();
    }

    public Optional<GamePlayer> findByUniqueId(UUID uuid) {
        return Optional.ofNullable(playerContainer.getEntity(uuid));
    }

    @NotNull
    public Collection<GamePlayer> getAll() {
        return playerContainer.getEntities();
    }

    public boolean isRegistered(@NotNull Player player) {
        return playerContainer.getEntity(player) != null;
    }

    public boolean isRegistered(@NotNull UUID uuid) {
        return playerContainer.getEntity(uuid) != null;
    }

    public void deregister(@NotNull UUID playerUuid) {
        playerContainer.removeEntity(playerUuid);
    }

    public GamePlayer registerEntity(Player player) {
        Hitbox hitbox = new PlayerHitbox(player);

        UUID playerId = player.getUniqueId();
        gameContextProvider.registerEntity(playerId, gameKey);

        GamePlayer gamePlayer = gamePlayerFactory.create(player, hitbox);
        playerContainer.addEntity(gamePlayer);

        return gamePlayer;
    }
}
