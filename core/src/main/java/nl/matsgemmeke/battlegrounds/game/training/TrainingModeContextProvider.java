package nl.matsgemmeke.battlegrounds.game.training;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.component.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import nl.matsgemmeke.battlegrounds.game.training.component.spawn.TrainingModeSpawnPointProviderFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentBehavior;
import nl.matsgemmeke.battlegrounds.item.gun.GunBehavior;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class TrainingModeContextProvider implements Provider<GameContext> {

    @NotNull
    private BattlegroundsConfiguration configuration;
    @NotNull
    private EventDispatcher eventDispatcher;
    @NotNull
    private GameContextProvider contextProvider;
    @NotNull
    private DefaultPlayerRegistryFactory playerRegistryFactory;
    @NotNull
    private TrainingModeSpawnPointProviderFactory spawnPointProviderFactory;

    @Inject
    public TrainingModeContextProvider(
            @NotNull BattlegroundsConfiguration configuration,
            @NotNull EventDispatcher eventDispatcher,
            @NotNull GameContextProvider contextProvider,
            @NotNull DefaultPlayerRegistryFactory playerRegistryFactory,
            @NotNull TrainingModeSpawnPointProviderFactory spawnPointProviderFactory
    ) {
        this.configuration = configuration;
        this.eventDispatcher = eventDispatcher;
        this.contextProvider = contextProvider;
        this.playerRegistryFactory = playerRegistryFactory;
        this.spawnPointProviderFactory = spawnPointProviderFactory;
    }

    public GameContext get() {
        TrainingMode trainingMode = new TrainingMode();
        trainingMode.addItemBehavior(new EquipmentBehavior(trainingMode.getEquipmentStorage()));
        trainingMode.addItemBehavior(new GunBehavior(trainingMode.getGunStorage()));

        PlayerRegistry playerRegistry = playerRegistryFactory.make(trainingMode.getPlayerStorage());
        SpawnPointProvider spawnPointProvider = spawnPointProviderFactory.make(trainingMode.getSpawnPointStorage());

        TrainingModeContext trainingModeContext = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);

        this.registerEventHandlers(trainingModeContext);
        this.registerPlayers(trainingModeContext);

        contextProvider.assignTrainingModeContext(trainingModeContext);

        return trainingModeContext;
    }

    private void registerEventHandlers(@NotNull GameContext trainingModeContext) {
        DamageProcessor damageProcessor = trainingModeContext.getDamageProcessor();
        DeploymentInfoProvider deploymentInfoProvider = trainingModeContext.getDeploymentInfoProvider();

        eventDispatcher.registerEventHandler(EntityDamageEvent.class, new EntityDamageEventHandler(damageProcessor, deploymentInfoProvider));
    }

    private void registerPlayers(@NotNull GameContext trainingModeContext) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = trainingModeContext.getPlayerRegistry().registerEntity(player);
            gamePlayer.setPassive(configuration.isEnabledRegisterPlayersAsPassive());
        }
    }
}
