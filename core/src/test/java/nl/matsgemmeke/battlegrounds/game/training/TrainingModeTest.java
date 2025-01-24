package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

public class TrainingModeTest {

    private InternalsProvider internals;
    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;
    private ItemStorage<Gun, GunHolder> gunStorage;

    @BeforeEach
    public void setUp() {
        internals = mock(InternalsProvider.class);
        equipmentStorage = new ItemStorage<>();
        gunStorage = new ItemStorage<>();
    }

    @Test
    public void shouldReturnInstanceOfGameContext() {
        TrainingMode trainingMode = new TrainingMode(internals, equipmentStorage, gunStorage);

        GameContext context = trainingMode.getContext();

        assertInstanceOf(TrainingModeContext.class, context);
    }
}
