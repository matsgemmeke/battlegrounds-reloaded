package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinEventHandler implements EventHandler<PlayerJoinEvent> {

    @NotNull
    private BattlegroundsConfiguration config;
    @NotNull
    private EntityRegistry<GamePlayer, Player> playerRegistry;

    public PlayerJoinEventHandler(@NotNull BattlegroundsConfiguration config, @NotNull EntityRegistry<GamePlayer, Player> playerRegistry) {
        this.config = config;
        this.playerRegistry = playerRegistry;
    }

    public void handle(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GamePlayer gamePlayer = playerRegistry.registerEntity(player);
        gamePlayer.setPassive(config.isEnabledRegisterPlayersAsPassive());
    }
}
