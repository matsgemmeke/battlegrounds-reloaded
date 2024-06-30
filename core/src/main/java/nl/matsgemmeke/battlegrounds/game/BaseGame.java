package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseGame implements Game {

    protected EntityRegister<GameItem> itemEntityRegister;
    protected Set<ItemBehavior> behaviors;

    public BaseGame() {
        this.behaviors = new HashSet<>();
    }

    public void addItemBehavior(@NotNull ItemBehavior behavior) {
        behaviors.add(behavior);
    }

    @NotNull
    public abstract Iterable<GamePlayer> getPlayers();

    @Nullable
    public GamePlayer getGamePlayer(@NotNull Player player) {
        for (GamePlayer gamePlayer : this.getPlayers()) {
            if (gamePlayer.getEntity() == player) {
                return gamePlayer;
            }
        }
        return null;
    }

    public boolean hasEntity(@NotNull Entity entity) {
        return false;
    }

    public boolean hasPlayer(@NotNull Player player) {
        return this.getGamePlayer(player) != null;
    }
}
