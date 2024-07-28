package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultTrainingModeTest {

    private InternalsProvider internals;
    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;
    private ItemStorage<Gun, GunHolder> gunStorage;

    @Before
    public void setUp() {
        internals = mock(InternalsProvider.class);
        equipmentStorage = new ItemStorage<>();
        gunStorage = new ItemStorage<>();
    }

    @Test
    public void shouldReturnInstanceOfGameContext() {
        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentStorage, gunStorage);

        GameContext context = trainingMode.getContext();

        assertTrue(context instanceof DefaultTrainingModeContext);
    }
}
