package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointContainer;
import org.jetbrains.annotations.NotNull;

public abstract class BaseGame implements Game {

    @NotNull
    protected EntityContainer<GamePlayer> playerContainer;
    @NotNull
    protected SpawnPointContainer spawnPointContainer;

    public BaseGame() {
        this.playerContainer = new EntityContainer<>();
        this.spawnPointContainer = new SpawnPointContainer();
    }

    @NotNull
    public EntityContainer<GamePlayer> getPlayerContainer() {
        return playerContainer;
    }

    @NotNull
    public SpawnPointContainer getSpawnPointContainer() {
        return spawnPointContainer;
    }
}
