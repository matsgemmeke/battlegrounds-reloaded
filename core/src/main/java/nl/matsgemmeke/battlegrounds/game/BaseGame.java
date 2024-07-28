package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
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

    @NotNull
    public EntityStorage<GameItem> getItemStorage() {
        return itemStorage;
    }

    @NotNull
    public EntityStorage<GamePlayer> getPlayerStorage() {
        return playerStorage;
    }

    public void addItemBehavior(@NotNull ItemBehavior itemBehavior) {
        itemBehaviors.add(itemBehavior);
    }
}
