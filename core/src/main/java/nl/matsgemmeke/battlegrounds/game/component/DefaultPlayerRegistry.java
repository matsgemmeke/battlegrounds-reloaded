package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public GamePlayer findByEntity(Player player) {
        for (GamePlayer gamePlayer : playerStorage.getEntities()) {
            if (gamePlayer.getEntity() == player) {
                return gamePlayer;
            }
        }
        return null;
    }

    public boolean isRegistered(Player entity) {
        return playerStorage.getEntity(entity) != null;
    }

    @NotNull
    public GamePlayer registerEntity(Player player) {
        GamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        playerStorage.addEntity(gamePlayer);

        return gamePlayer;
    }
}
