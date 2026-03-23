package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointContainer;
import nl.matsgemmeke.battlegrounds.item.action.ActionExecutor;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseGame implements Game {

    @NotNull
    protected EntityContainer<GamePlayer> playerContainer;
    @NotNull
    protected ItemContainer<Gun, GunHolder> gunContainer;
    @NotNull
    protected Set<ActionExecutor> actionExecutors;
    @NotNull
    protected SpawnPointContainer spawnPointContainer;

    public BaseGame() {
        this.gunContainer = new ItemContainer<>();
        this.actionExecutors = new HashSet<>();
        this.playerContainer = new EntityContainer<>();
        this.spawnPointContainer = new SpawnPointContainer();
    }

    @NotNull
    public ItemContainer<Gun, GunHolder> getGunContainer() {
        return gunContainer;
    }

    @NotNull
    public Set<ActionExecutor> getActionExecutors() {
        return actionExecutors;
    }

    @NotNull
    public EntityContainer<GamePlayer> getPlayerContainer() {
        return playerContainer;
    }

    @NotNull
    public SpawnPointContainer getSpawnPointContainer() {
        return spawnPointContainer;
    }

    public void addActionExecutor(@NotNull ActionExecutor actionExecutor) {
        actionExecutors.add(actionExecutor);
    }
}
