package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeTargetFinder;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class DefaultTrainingModeContextTest {

    private InternalsProvider internals;
    private TrainingMode trainingMode;

    @Before
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

        assertTrue(actionHandler instanceof DefaultActionHandler);
    }

    @Test
    public void shouldReturnNewInstanceOfAudioEmitter() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        AudioEmitter audioEmitter = context.getAudioEmitter();

        assertTrue(audioEmitter instanceof DefaultAudioEmitter);
    }

    @Test
    public void shouldReturnNewInstanceOfCollisionDetector() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        CollisionDetector collisionDetector = context.getCollisionDetector();

        assertTrue(collisionDetector instanceof DefaultCollisionDetector);
    }

    @Test
    public void shouldReturnInstanceOfDamageProcessor() {
        EntityStorage<GameItem> itemStorage = (EntityStorage<GameItem>) mock(EntityStorage.class);
        when(trainingMode.getItemStorage()).thenReturn(itemStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        DamageProcessor damageProcessor = context.getDamageProcessor();

        assertTrue(damageProcessor instanceof TrainingModeDamageProcessor);
    }

    @Test
    public void shouldReturnNewInstanceOfItemRegistryForEquipmentItems() {
        ItemStorage<Equipment, EquipmentHolder> equipmentStorage = (ItemStorage<Equipment, EquipmentHolder>) mock(ItemStorage.class);
        when(trainingMode.getEquipmentStorage()).thenReturn(equipmentStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        ItemRegistry<Equipment, EquipmentHolder> equipmentRegistry = context.getEquipmentRegistry();

        assertTrue(equipmentRegistry instanceof DefaultEquipmentRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfEntityRegisterForItemEntities() {
        EntityStorage<GameItem> itemEntityStorage = (EntityStorage<GameItem>) mock(EntityStorage.class);
        when(trainingMode.getItemStorage()).thenReturn(itemEntityStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        EntityRegistry<GameItem, Item> itemRegistry = context.getItemRegistry();

        assertTrue(itemRegistry instanceof DefaultItemRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfItemRegistryForGunItems() {
        ItemStorage<Gun, GunHolder> gunStorage = (ItemStorage<Gun, GunHolder>) mock(ItemStorage.class);
        when(trainingMode.getGunStorage()).thenReturn(gunStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        ItemRegistry<Gun, GunHolder> gunRegistry = context.getGunRegistry();

        assertTrue(gunRegistry instanceof DefaultGunRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfEntityRegisterForPlayerEntities() {
        EntityStorage<GamePlayer> playerStorage = (EntityStorage<GamePlayer>) mock(EntityStorage.class);
        when(trainingMode.getPlayerStorage()).thenReturn(playerStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        EntityRegistry<GamePlayer, Player> playerRegistry = context.getPlayerRegistry();

        assertTrue(playerRegistry instanceof DefaultPlayerRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfTrainingModeTargetFinder() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, internals);
        TargetFinder targetFinder = context.getTargetFinder();

        assertTrue(targetFinder instanceof TrainingModeTargetFinder);
    }
}
