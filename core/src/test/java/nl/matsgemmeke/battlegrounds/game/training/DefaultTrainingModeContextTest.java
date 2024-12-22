package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DefaultDeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.DefaultGunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultEquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import nl.matsgemmeke.battlegrounds.game.storage.DeploymentObjectStorage;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeTargetFinder;
import nl.matsgemmeke.battlegrounds.game.training.component.spawn.TrainingModeSpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class DefaultTrainingModeContextTest {

    private InternalsProvider internals;
    private TrainingMode trainingMode;

    @BeforeEach
    public void setUp() {
        internals = mock(InternalsProvider.class);
        trainingMode = mock(TrainingMode.class);

        EntityStorage<GamePlayer> playerStorage = new EntityStorage<>();
        when(trainingMode.getPlayerStorage()).thenReturn(playerStorage);
    }

    @Test
    public void shouldReturnInstanceOfActionHandler() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        ActionHandler actionHandler = context.getActionHandler();

        assertInstanceOf(DefaultActionHandler.class, actionHandler);
    }

    @Test
    public void shouldReturnNewInstanceOfAudioEmitter() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        AudioEmitter audioEmitter = context.getAudioEmitter();

        assertInstanceOf(DefaultAudioEmitter.class, audioEmitter);
    }

    @Test
    public void shouldReturnNewInstanceOfCollisionDetector() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        CollisionDetector collisionDetector = context.getCollisionDetector();

        assertInstanceOf(DefaultCollisionDetector.class, collisionDetector);
    }

    @Test
    public void getDeploymentObjectRegistryReturnsNewInstanceOfTheDefaultImplementation() {
        DeploymentObjectStorage deploymentObjectStorage = new DeploymentObjectStorage();
        when(trainingMode.getDeploymentObjectStorage()).thenReturn(deploymentObjectStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        DeploymentObjectRegistry deploymentObjectRegistry = context.getDeploymentObjectRegistry();

        assertInstanceOf(DefaultDeploymentObjectRegistry.class, deploymentObjectRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfItemRegistryForEquipmentItems() {
        ItemStorage<Equipment, EquipmentHolder> equipmentStorage = new ItemStorage<>();
        when(trainingMode.getEquipmentStorage()).thenReturn(equipmentStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        EquipmentRegistry equipmentRegistry = context.getEquipmentRegistry();

        assertInstanceOf(DefaultEquipmentRegistry.class, equipmentRegistry);
    }

    @Test
    public void getGunInfoProviderReturnsNewInstanceOfTheDefaultImplementation() {
        ItemStorage<Gun, GunHolder> gunStorage = (ItemStorage<Gun, GunHolder>) mock(ItemStorage.class);
        when(trainingMode.getGunStorage()).thenReturn(gunStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        GunInfoProvider gunInfoProvider = context.getGunInfoProvider();

        assertInstanceOf(DefaultGunInfoProvider.class, gunInfoProvider);
    }

    @Test
    public void shouldReturnNewInstanceOfItemRegistryForGunItems() {
        ItemStorage<Gun, GunHolder> gunStorage = (ItemStorage<Gun, GunHolder>) mock(ItemStorage.class);
        when(trainingMode.getGunStorage()).thenReturn(gunStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        ItemRegistry<Gun, GunHolder> gunRegistry = context.getGunRegistry();

        assertInstanceOf(DefaultGunRegistry.class, gunRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfEntityRegisterForPlayerEntities() {
        EntityStorage<GamePlayer> playerStorage = (EntityStorage<GamePlayer>) mock(EntityStorage.class);
        when(trainingMode.getPlayerStorage()).thenReturn(playerStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        EntityRegistry<GamePlayer, Player> playerRegistry = context.getPlayerRegistry();

        assertInstanceOf(DefaultPlayerRegistry.class, playerRegistry);
    }

    @Test
    public void getSpawnPointProviderReturnsNewInstanceOfDefaultImplementation() {
        SpawnPointStorage spawnPointStorage = new SpawnPointStorage();
        when(trainingMode.getSpawnPointStorage()).thenReturn(spawnPointStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        SpawnPointProvider spawnPointProvider = context.getSpawnPointProvider();

        assertInstanceOf(TrainingModeSpawnPointProvider.class, spawnPointProvider);
    }

    @Test
    public void shouldReturnNewInstanceOfTrainingModeTargetFinder() {
        DeploymentObjectStorage deploymentObjectStorage = new DeploymentObjectStorage();
        when(trainingMode.getDeploymentObjectStorage()).thenReturn(deploymentObjectStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        TargetFinder targetFinder = context.getTargetFinder();

        assertInstanceOf(TrainingModeTargetFinder.class, targetFinder);
    }
}
