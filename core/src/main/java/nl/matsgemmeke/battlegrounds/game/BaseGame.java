package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseGame implements Game {

    @NotNull
    protected EntityStorage<GameItem> itemStorage;
    @NotNull
    protected EntityStorage<GamePlayer> playerStorage;
    @NotNull
    protected Set<ItemBehavior> itemBehaviors;

    public BaseGame() {
        this.itemBehaviors = new HashSet<>();
        this.itemStorage = new EntityStorage<>();
        this.playerStorage = new EntityStorage<>();
    }

    @NotNull
    public Set<ItemBehavior> getItemBehaviors() {
        return itemBehaviors;
    }

    public void addItemBehavior(@NotNull ItemBehavior itemBehavior) {
        itemBehaviors.add(itemBehavior);
    }

    @Nullable
    public GameEntity getGameEntity(@NotNull Entity entity) {
        for (EntityStorage<?> entityStorage : this.getEntityStorages()) {
            var foundEntity = entityStorage.getEntity(entity);
            if (foundEntity != null) {
                return foundEntity;
            }
        }
        return null;
    }

    @NotNull
    private Iterable<EntityStorage<?>> getEntityStorages() {
        return List.of(itemStorage, playerStorage);
    }

    @Nullable
    public GamePlayer getGamePlayer(@NotNull Player player) {
        return playerStorage.getEntity(player);
    }

    public boolean hasEntity(@NotNull Entity entity) {
        return this.getGameEntity(entity) != null;
    }

    public boolean hasPlayer(@NotNull Player player) {
        return this.getGamePlayer(player) != null;
    }
}
