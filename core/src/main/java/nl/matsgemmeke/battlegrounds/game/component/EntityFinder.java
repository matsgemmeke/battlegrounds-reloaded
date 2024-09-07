package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityFinder {

    @NotNull
    private EntityRegistry<GameMob, Mob> mobRegistry;
    @NotNull
    private EntityRegistry<GamePlayer, Player> playerRegistry;

    public EntityFinder(@NotNull EntityRegistry<GameMob, Mob> mobRegistry, @NotNull EntityRegistry<GamePlayer, Player> playerRegistry) {
        this.mobRegistry = mobRegistry;
        this.playerRegistry = playerRegistry;
    }

    @Nullable
    public GameEntity findEntity(@NotNull Entity entity) {
        for (EntityRegistry<?, ?> registry : this.getEntityRegistries()) {
            GameEntity gameEntity = registry.findByEntity(entity);
            if (gameEntity != null) {
                return gameEntity;
            }
        }
        return null;
    }

    @NotNull
    private List<EntityRegistry<?, ?>> getEntityRegistries() {
        return List.of(this.mobRegistry, this.playerRegistry);
    }
}
