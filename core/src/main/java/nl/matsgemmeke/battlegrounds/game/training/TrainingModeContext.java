package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DefaultDeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.DefaultGunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultEquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultGunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.registry.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.training.component.damage.TrainingModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeTargetFinder;
import org.jetbrains.annotations.NotNull;

public class TrainingModeContext implements GameContext {

    @NotNull
    private ActionHandler actionHandler;
    @NotNull
    private DamageProcessor damageProcessor;
    @NotNull
    private PlayerRegistry playerRegistry;
    @NotNull
    private SpawnPointProvider spawnPointProvider;
    @NotNull
    private TrainingMode trainingMode;

    public TrainingModeContext(
            @NotNull TrainingMode trainingMode,
            @NotNull PlayerRegistry playerRegistry,
            @NotNull SpawnPointProvider spawnPointProvider
    ) {
        this.trainingMode = trainingMode;
        this.playerRegistry = playerRegistry;
        this.spawnPointProvider = spawnPointProvider;
        this.actionHandler = this.setUpActionHandlerInstance();
        this.damageProcessor = this.setUpDamageProcessorInstance();
    }

    private ActionHandler setUpActionHandlerInstance() {
        return new DefaultActionHandler(trainingMode, playerRegistry);
    }

    private DamageProcessor setUpDamageProcessorInstance() {
        DeploymentInfoProvider deploymentInfoProvider = this.getDeploymentInfoProvider();

        return new TrainingModeDamageProcessor(GameKey.ofTrainingMode(), deploymentInfoProvider);
    }

    @NotNull
    public ActionHandler getActionHandler() {
        return actionHandler;
    }

    @NotNull
    public AudioEmitter getAudioEmitter() {
        return new DefaultAudioEmitter();
    }

    @NotNull
    public CollisionDetector getCollisionDetector() {
        BlockCollisionChecker blockCollisionChecker = new BlockCollisionChecker();

        return new DefaultCollisionDetector(blockCollisionChecker);
    }

    @NotNull
    public DamageProcessor getDamageProcessor() {
        return damageProcessor;
    }

    @NotNull
    public DeploymentInfoProvider getDeploymentInfoProvider() {
        EquipmentRegistry equipmentRegistry = this.getEquipmentRegistry();

        return new DefaultDeploymentInfoProvider(equipmentRegistry);
    }

    @NotNull
    public EquipmentRegistry getEquipmentRegistry() {
        return new DefaultEquipmentRegistry(trainingMode.getEquipmentStorage());
    }

    @NotNull
    public GunInfoProvider getGunInfoProvider() {
        return new DefaultGunInfoProvider(trainingMode.getGunStorage());
    }

    @NotNull
    public GunRegistry getGunRegistry() {
        return new DefaultGunRegistry(trainingMode.getGunStorage());
    }

    @NotNull
    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    @NotNull
    public SpawnPointProvider getSpawnPointProvider() {
        return spawnPointProvider;
    }

    @NotNull
    public TargetFinder getTargetFinder() {
        DeploymentInfoProvider deploymentInfoProvider = this.getDeploymentInfoProvider();

        return new TrainingModeTargetFinder(deploymentInfoProvider, trainingMode.getPlayerStorage());
    }
}
