package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.DefaultGunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultEquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultGunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.registry.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import nl.matsgemmeke.battlegrounds.game.training.component.damage.TrainingModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeTargetFinder;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModeContextTest {

    private TrainingMode trainingMode;
    private PlayerRegistry playerRegistry;
    private SpawnPointProvider spawnPointProvider;

    @BeforeEach
    public void setUp() {
        trainingMode = mock(TrainingMode.class);
        playerRegistry = mock(PlayerRegistry.class);
        spawnPointProvider = mock(SpawnPointProvider.class);

        ItemStorage<Equipment, EquipmentHolder> equipmentStorage = new ItemStorage<>();
        EntityStorage<GamePlayer> playerStorage = new EntityStorage<>();

        when(trainingMode.getEquipmentStorage()).thenReturn(equipmentStorage);
        when(trainingMode.getPlayerStorage()).thenReturn(playerStorage);
    }

    @Test
    public void shouldReturnInstanceOfActionHandler() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        ActionHandler actionHandler = context.getActionHandler();

        assertInstanceOf(DefaultActionHandler.class, actionHandler);
    }

    @Test
    public void shouldReturnNewInstanceOfAudioEmitter() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        AudioEmitter audioEmitter = context.getAudioEmitter();

        assertInstanceOf(DefaultAudioEmitter.class, audioEmitter);
    }

    @Test
    public void shouldReturnNewInstanceOfCollisionDetector() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        CollisionDetector collisionDetector = context.getCollisionDetector();

        assertInstanceOf(DefaultCollisionDetector.class, collisionDetector);
    }

    @Test
    public void getDamageProcessorReturnsExistingInstanceOfTrainingModeDamageProcessor() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        DamageProcessor damageProcessor = context.getDamageProcessor();

        assertInstanceOf(TrainingModeDamageProcessor.class, damageProcessor);
    }

    @Test
    public void shouldReturnNewInstanceOfItemRegistryForEquipmentItems() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        EquipmentRegistry equipmentRegistry = context.getEquipmentRegistry();

        assertInstanceOf(DefaultEquipmentRegistry.class, equipmentRegistry);
    }

    @Test
    public void getGunInfoProviderReturnsNewInstanceOfTheDefaultImplementation() {
        ItemStorage<Gun, GunHolder> gunStorage = new ItemStorage<>();
        when(trainingMode.getGunStorage()).thenReturn(gunStorage);

        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        GunInfoProvider gunInfoProvider = context.getGunInfoProvider();

        assertInstanceOf(DefaultGunInfoProvider.class, gunInfoProvider);
    }

    @Test
    public void shouldReturnNewInstanceOfItemRegistryForGunItems() {
        ItemStorage<Gun, GunHolder> gunStorage = new ItemStorage<>();
        when(trainingMode.getGunStorage()).thenReturn(gunStorage);

        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        GunRegistry gunRegistry = context.getGunRegistry();

        assertInstanceOf(DefaultGunRegistry.class, gunRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfEntityRegisterForPlayerEntities() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        PlayerRegistry result = context.getPlayerRegistry();

        assertEquals(playerRegistry, result);
    }

    @Test
    public void getSpawnPointProviderReturnsInstance() {
        SpawnPointStorage spawnPointStorage = new SpawnPointStorage();
        when(trainingMode.getSpawnPointStorage()).thenReturn(spawnPointStorage);

        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        SpawnPointProvider result = context.getSpawnPointProvider();

        assertEquals(spawnPointProvider, result);
    }

    @Test
    public void shouldReturnNewInstanceOfTrainingModeTargetFinder() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, playerRegistry, spawnPointProvider);
        TargetFinder targetFinder = context.getTargetFinder();

        assertInstanceOf(TrainingModeTargetFinder.class, targetFinder);
    }
}
