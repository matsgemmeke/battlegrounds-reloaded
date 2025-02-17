package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinEventHandler implements EventHandler<PlayerJoinEvent> {

    @NotNull
    private final BattlegroundsConfiguration config;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final GameKey trainingModeGameKey;

    @Inject
    public PlayerJoinEventHandler(
            @NotNull BattlegroundsConfiguration config,
            @NotNull GameContextProvider contextProvider,
            @Named("TrainingMode") @NotNull GameKey trainingModeGameKey
    ) {
        this.config = config;
        this.contextProvider = contextProvider;
        this.trainingModeGameKey = trainingModeGameKey;
    }

    public void handle(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerRegistry playerRegistry = contextProvider.getComponent(trainingModeGameKey, PlayerRegistry.class);

        GamePlayer gamePlayer = playerRegistry.registerEntity(player);
        gamePlayer.setPassive(config.isEnabledRegisterPlayersAsPassive());
    }
}
