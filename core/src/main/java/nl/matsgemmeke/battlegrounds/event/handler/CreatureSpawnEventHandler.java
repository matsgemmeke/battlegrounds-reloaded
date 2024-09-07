package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class CreatureSpawnEventHandler implements EventHandler<CreatureSpawnEvent> {

    @NotNull
    private BattlegroundsConfiguration config;
    @NotNull
    private GameContext trainingModeContext;

    public CreatureSpawnEventHandler(@NotNull GameContext trainingModeContext, @NotNull BattlegroundsConfiguration config) {
        this.trainingModeContext = trainingModeContext;
        this.config = config;
    }

    public void handle(@NotNull CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (!config.isEnabledRegisterEntitiesToTrainingModeUponSpawn()) {
            return;
        }

        trainingModeContext.getMobRegistry().registerEntity(mob);
    }
}
