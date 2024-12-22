package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DefaultDeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.DefaultGunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultEquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultGunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeTargetFinder;
import nl.matsgemmeke.battlegrounds.game.training.component.spawn.TrainingModeSpawnPointProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultTrainingModeContext implements GameContext {

    @NotNull
    private ActionHandler actionHandler;
    @NotNull
    private DamageProcessor damageProcessor;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private TrainingMode trainingMode;

    public DefaultTrainingModeContext(@NotNull TrainingMode trainingMode, @NotNull InternalsProvider internals) {
        this.trainingMode = trainingMode;
        this.internals = internals;
        this.actionHandler = this.setUpActionHandlerInstance();
        this.damageProcessor = new TrainingModeDamageProcessor(this);
    }

    private ActionHandler setUpActionHandlerInstance() {
        EntityRegistry<GamePlayer, Player> playerRegistry = new DefaultPlayerRegistry(trainingMode.getPlayerStorage(), internals);

        return new DefaultActionHandler(trainingMode, playerRegistry);
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

    public void setDamageProcessor(@NotNull DamageProcessor damageProcessor) {
        this.damageProcessor = damageProcessor;
    }

    @NotNull
    public DeploymentObjectRegistry getDeploymentObjectRegistry() {
        return new DefaultDeploymentObjectRegistry(trainingMode.getDeploymentObjectStorage());
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
    public EntityRegistry<GamePlayer, Player> getPlayerRegistry() {
        return new DefaultPlayerRegistry(trainingMode.getPlayerStorage(), internals);
    }

    @NotNull
    public SpawnPointProvider getSpawnPointProvider() {
        return new TrainingModeSpawnPointProvider(trainingMode.getSpawnPointStorage());
    }

    @NotNull
    public TargetFinder getTargetFinder() {
        return new TrainingModeTargetFinder(trainingMode.getDeploymentObjectStorage(), trainingMode.getPlayerStorage());
    }
}
