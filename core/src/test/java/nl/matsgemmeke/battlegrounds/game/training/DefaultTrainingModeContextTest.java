package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeCollisionDetector;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultTrainingModeContextTest {

    private TrainingMode trainingMode;

    @Before
    public void setUp() {
        trainingMode = mock(TrainingMode.class);
    }

    @Test
    public void shouldReturnNewInstanceOfAudioEmitter() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode);

        AudioEmitter audioEmitter = context.getAudioEmitter();

        assertTrue(audioEmitter instanceof DefaultAudioEmitter);
    }

    @Test
    public void shouldReturnNewInstanceOfCollisionDetector() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode);

        CollisionDetector collisionDetector = context.getCollisionDetector();

        assertTrue(collisionDetector instanceof TrainingModeCollisionDetector);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnNewInstanceOfItemRegistryForEquipmentItems() {
        ItemStorage<Equipment, EquipmentHolder> equipmentStorage = (ItemStorage<Equipment, EquipmentHolder>) mock(ItemStorage.class);
        when(trainingMode.getEquipmentStorage()).thenReturn(equipmentStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode);

        ItemRegistry<Equipment, EquipmentHolder> equipmentRegistry = context.getEquipmentRegistry();

        assertTrue(equipmentRegistry instanceof DefaultEquipmentRegistry);
    }

    @Test
    public void shouldReturnNewInstanceOfEntityRegister() {
        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode);

        EntityRegistry<Item, GameItem> itemRegistry = context.getItemRegistry();

        assertTrue(itemRegistry instanceof DefaultItemRegistry);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnNewInstanceOfItemRegistryForGunItems() {
        ItemStorage<Gun, GunHolder> gunStorage = (ItemStorage<Gun, GunHolder>) mock(ItemStorage.class);
        when(trainingMode.getGunStorage()).thenReturn(gunStorage);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode);

        ItemRegistry<Gun, GunHolder> gunRegistry = context.getGunRegistry();

        assertTrue(gunRegistry instanceof DefaultGunRegistry);
    }
}
