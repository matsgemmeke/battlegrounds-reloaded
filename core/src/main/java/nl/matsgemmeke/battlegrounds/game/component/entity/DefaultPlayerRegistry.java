package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class DefaultPlayerRegistry implements PlayerRegistry {

    @NotNull
    private final DefaultGamePlayerFactory gamePlayerFactory;
    @NotNull
    private final EntityContainer<GamePlayer> playerContainer;

    @Inject
    public DefaultPlayerRegistry(@NotNull DefaultGamePlayerFactory gamePlayerFactory, @Assisted @NotNull EntityContainer<GamePlayer> playerContainer) {
        this.gamePlayerFactory = gamePlayerFactory;
        this.playerContainer = playerContainer;
    }

    @Nullable
    public GamePlayer findByEntity(@NotNull Player player) {
        for (GamePlayer gamePlayer : playerContainer.getEntities()) {
            if (gamePlayer.getEntity() == player) {
                return gamePlayer;
            }
        }
        return null;
    }

    @Nullable
    public GamePlayer findByUUID(@NotNull UUID uuid) {
        return playerContainer.getEntity(uuid);
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

    @NotNull
    public GamePlayer registerEntity(@NotNull Player player) {
        GamePlayer gamePlayer = gamePlayerFactory.create(player);

        playerContainer.addEntity(gamePlayer);

        return gamePlayer;
    }
}
