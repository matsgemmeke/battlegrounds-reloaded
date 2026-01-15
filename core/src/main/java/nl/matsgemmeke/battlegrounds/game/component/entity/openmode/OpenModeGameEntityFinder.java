package nl.matsgemmeke.battlegrounds.game.component.entity.openmode;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.util.BukkitEntityFinder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.Optional;
import java.util.UUID;

public class OpenModeGameEntityFinder implements GameEntityFinder {

    private final BukkitEntityFinder bukkitEntityFinder;
    private final MobRegistry mobRegistry;
    private final PlayerRegistry playerRegistry;

    @Inject
    public OpenModeGameEntityFinder(MobRegistry mobRegistry, PlayerRegistry playerRegistry, BukkitEntityFinder bukkitEntityFinder) {
        this.mobRegistry = mobRegistry;
        this.playerRegistry = playerRegistry;
        this.bukkitEntityFinder = bukkitEntityFinder;
    }

    @Override
    public Optional<GameEntity> findGameEntityByUniqueId(UUID uniqueId) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(uniqueId).orElse(null);

        if (gamePlayer != null) {
            return Optional.of(gamePlayer);
        }

        GameMob gameMob = mobRegistry.findByUniqueId(uniqueId).orElse(null);

        if (gameMob != null) {
            return Optional.of(gameMob);
        }

        Entity entity = bukkitEntityFinder.getEntityByUniqueId(uniqueId).orElse(null);

        if (entity instanceof Mob mob) {
            return Optional.of(mobRegistry.register(mob));
        }

        return Optional.empty();
    }
}
