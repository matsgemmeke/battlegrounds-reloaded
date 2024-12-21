package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseGame implements Game {

    @NotNull
    protected EntityStorage<GamePlayer> playerStorage;
    @NotNull
    protected Set<ItemBehavior> itemBehaviors;
    @NotNull
    protected SpawnPointStorage spawnPointStorage;

    public BaseGame() {
        this.itemBehaviors = new HashSet<>();
        this.playerStorage = new EntityStorage<>();
        this.spawnPointStorage = new SpawnPointStorage();
    }

    @NotNull
    public Set<ItemBehavior> getItemBehaviors() {
        return itemBehaviors;
    }

    @NotNull
    public EntityStorage<GamePlayer> getPlayerStorage() {
        return playerStorage;
    }

    @NotNull
    public SpawnPointStorage getSpawnPointStorage() {
        return spawnPointStorage;
    }

    public void addItemBehavior(@NotNull ItemBehavior itemBehavior) {
        itemBehaviors.add(itemBehavior);
    }
}
