package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DefaultPlayerRegistry implements EntityRegistry<GamePlayer, Player> {

    @NotNull
    private EntityStorage<GamePlayer> playerStorage;
    @NotNull
    private InternalsProvider internals;

    public DefaultPlayerRegistry(@NotNull EntityStorage<GamePlayer> playerStorage, @NotNull InternalsProvider internals) {
        this.playerStorage = playerStorage;
        this.internals = internals;
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

    public boolean isRegistered(@NotNull Player player) {
        return playerStorage.getEntity(player) != null;
    }

    public boolean isRegistered(@NotNull UUID uuid) {
        return playerStorage.getEntity(uuid) != null;
    }

    @NotNull
    public GamePlayer registerEntity(@NotNull Player player) {
        GamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        playerStorage.addEntity(gamePlayer);

        return gamePlayer;
    }
}
