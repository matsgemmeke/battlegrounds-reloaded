package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
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
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPointStorage;
import nl.matsgemmeke.battlegrounds.game.training.component.damage.TrainingModeDamageProcessor;
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

public class TrainingModeContextTest {

    private InternalsProvider internals;
    private TrainingMode trainingMode;

    @BeforeEach
    public void setUp() {
        internals = mock(InternalsProvider.class);
        trainingMode = mock(TrainingMode.class);

        ItemStorage<Equipment, EquipmentHolder> equipmentStorage = new ItemStorage<>();
        EntityStorage<GamePlayer> playerStorage = new EntityStorage<>();

        when(trainingMode.getEquipmentStorage()).thenReturn(equipmentStorage);
        when(trainingMode.getPlayerStorage()).thenReturn(playerStorage);
    }

    @Test
    public void shouldReturnInstanceOfActionHandler() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        ActionHandler actionHandler = context.getActionHandler();

        assertInstanceOf(DefaultActionHandler.class, actionHandler);
    }

    @Test
    public void shouldReturnNewInstanceOfAudioEmitter() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        AudioEmitter audioEmitter = context.getAudioEmitter();

        assertInstanceOf(DefaultAudioEmitter.class, audioEmitter);
    }

    @Test
    public void shouldReturnNewInstanceOfCollisionDetector() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        CollisionDetector collisionDetector = context.getCollisionDetector();

        assertInstanceOf(DefaultCollisionDetector.class, collisionDetector);
    }

    @Test
    public void getDamageProcessorReturnsExistingInstanceOfTrainingModeDamageProcessor() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        DamageProcessor damageProcessor = context.getDamageProcessor();

        assertInstanceOf(TrainingModeDamageProcessor.class, damageProcessor);
    }

    @Test
    public void shouldReturnNewInstanceOfItemRegistryForEquipmentItems() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        EquipmentRegistry equipmentRegistry = context.getEquipmentRegistry();

        assertInstanceOf(DefaultEquipmentRegistry.class, equipmentRegistry);
    }

    @Test
    public void getGunInfoProviderReturnsNewInstanceOfTheDefaultImplementation() {
        ItemStorage<Gun, GunHolder> gunStorage = new ItemStorage<>();
        when(trainingMode.getGunStorage()).thenReturn(gunStorage);

        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        GunInfoProvider gunInfoProvider = context.getGunInfoProvider();

        assertInstanceOf(DefaultGunInfoProvider.class, gunInfoProvider);
    }

    @Test
    public void shouldReturnNewInstanceOfItemRegistryForGunItems() {
        ItemStorage<Gun, GunHolder> gunStorage = new ItemStorage<>();
        when(trainingMode.getGunStorage()).thenReturn(gunStorage);

        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        GunRegistry gunRegistry = context.getGunRegistry();

        assertInstanceOf(DefaultGunRegistry.class, gunRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfEntityRegisterForPlayerEntities() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        EntityRegistry<GamePlayer, Player> playerRegistry = context.getPlayerRegistry();

        assertInstanceOf(DefaultPlayerRegistry.class, playerRegistry);
    }

    @Test
    public void getSpawnPointProviderReturnsNewInstanceOfDefaultImplementation() {
        SpawnPointStorage spawnPointStorage = new SpawnPointStorage();
        when(trainingMode.getSpawnPointStorage()).thenReturn(spawnPointStorage);

        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        SpawnPointProvider spawnPointProvider = context.getSpawnPointProvider();

        assertInstanceOf(TrainingModeSpawnPointProvider.class, spawnPointProvider);
    }

    @Test
    public void shouldReturnNewInstanceOfTrainingModeTargetFinder() {
        TrainingModeContext context = new TrainingModeContext(trainingMode, internals);
        TargetFinder targetFinder = context.getTargetFinder();

        assertInstanceOf(TrainingModeTargetFinder.class, targetFinder);
    }
}
