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
    protected EntityRegister<GameItem> itemEntityRegister;
    @NotNull
    protected EntityRegister<GamePlayer> playerRegister;
    @NotNull
    protected Set<ItemBehavior> behaviors;

    public BaseGame() {
        this.behaviors = new HashSet<>();
        this.itemEntityRegister = new EntityRegister<>();
        this.playerRegister = new EntityRegister<>();
    }

    public void addItemBehavior(@NotNull ItemBehavior behavior) {
        behaviors.add(behavior);
    }

    @Nullable
    public GameEntity getGameEntity(@NotNull Entity entity) {
        for (EntityRegister<?> entityRegister : this.getEntityRegisters()) {
            var foundEntity = entityRegister.getEntity(entity);
            if (foundEntity != null) {
                return foundEntity;
            }
        }
        return null;
    }

    @NotNull
    private Iterable<EntityRegister<?>> getEntityRegisters() {
        return List.of(itemEntityRegister, playerRegister);
    }

    @Nullable
    public GamePlayer getGamePlayer(@NotNull Player player) {
        return playerRegister.getEntity(player);
    }

    public boolean hasEntity(@NotNull Entity entity) {
        return this.getGameEntity(entity) != null;
    }

    public boolean hasPlayer(@NotNull Player player) {
        return this.getGamePlayer(player) != null;
    }
}
