package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class DefaultPlayerRegistry implements PlayerRegistry {

    @NotNull
    private final DefaultGamePlayerFactory gamePlayerFactory;
    @NotNull
    private final EntityStorage<GamePlayer> playerStorage;

    @Inject
    public DefaultPlayerRegistry(@NotNull DefaultGamePlayerFactory gamePlayerFactory, @Assisted @NotNull EntityStorage<GamePlayer> playerStorage) {
        this.gamePlayerFactory = gamePlayerFactory;
        this.playerStorage = playerStorage;
    }

    @Nullable
    public GamePlayer findByEntity(@NotNull Player player) {
        for (GamePlayer gamePlayer : playerStorage.getEntities()) {
            if (gamePlayer.getEntity() == player) {
                return gamePlayer;
            }
        }
        return null;
    }

    @Nullable
    public GamePlayer findByUUID(@NotNull UUID uuid) {
        return playerStorage.getEntity(uuid);
    }

    @NotNull
    public Collection<GamePlayer> getAll() {
        return playerStorage.getEntities();
    }

    public boolean isRegistered(@NotNull Player player) {
        return playerStorage.getEntity(player) != null;
    }

    public boolean isRegistered(@NotNull UUID uuid) {
        return playerStorage.getEntity(uuid) != null;
    }

    public void deregister(@NotNull UUID playerUuid) {
        playerStorage.removeEntity(playerUuid);
    }

    @NotNull
    public GamePlayer registerEntity(@NotNull Player player) {
        GamePlayer gamePlayer = gamePlayerFactory.create(player);

        playerStorage.addEntity(gamePlayer);

        return gamePlayer;
    }
}
