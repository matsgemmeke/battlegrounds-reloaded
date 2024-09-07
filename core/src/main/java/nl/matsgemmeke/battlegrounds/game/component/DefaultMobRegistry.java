package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameMob;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DefaultMobRegistry implements EntityRegistry<GameMob, Mob> {

    @NotNull
    private EntityStorage<GameMob> mobStorage;

    public DefaultMobRegistry(@NotNull EntityStorage<GameMob> mobStorage) {
        this.mobStorage = mobStorage;
    }

    @Nullable
    public GameMob findByEntity(@NotNull Mob mob) {
        return mobStorage.getEntity(mob);
    }

    @Nullable
    public GameMob findByUUID(@NotNull UUID uuid) {
        return mobStorage.getEntity(uuid);
    }

    public boolean isRegistered(@NotNull Mob mob) {
        return mobStorage.getEntity(mob) != null;
    }

    public boolean isRegistered(@NotNull UUID uuid) {
        return mobStorage.getEntity(uuid) != null;
    }

    @NotNull
    public GameMob registerEntity(@NotNull Mob mob) {
        GameMob gameMob = new DefaultGameMob(mob);

        mobStorage.addEntity(gameMob);

        return gameMob;
    }
}
