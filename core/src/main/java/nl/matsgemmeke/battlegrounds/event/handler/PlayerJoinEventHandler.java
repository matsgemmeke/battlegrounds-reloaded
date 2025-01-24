package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinEventHandler implements EventHandler<PlayerJoinEvent> {

    @NotNull
    private BattlegroundsConfiguration config;
    @NotNull
    private GameContext trainingModeContext;

    @Inject
    public PlayerJoinEventHandler(@NotNull BattlegroundsConfiguration config, @Named("TrainingMode") @NotNull GameContext trainingModeContext) {
        this.config = config;
        this.trainingModeContext = trainingModeContext;
    }

    public void handle(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GamePlayer gamePlayer = trainingModeContext.getPlayerRegistry().registerEntity(player);
        gamePlayer.setPassive(config.isEnabledRegisterPlayersAsPassive());
    }
}
