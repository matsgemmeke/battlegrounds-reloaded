package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class EquipmentControlsFactoryTest {

    private Equipment equipment;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private NamespacedKeyCreator namespacedKeyCreator;
    private Section controlsSection;
    private Section rootSection;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();

        equipment = mock(Equipment.class);
        when(equipment.getName()).thenReturn("test equipment");

        namespacedKeyCreator = mock(NamespacedKeyCreator.class);
        when(namespacedKeyCreator.create("battlegrounds-equipment")).thenReturn(mock(NamespacedKey.class));

        controlsSection = mock(Section.class);
        rootSection = mock(Section.class);
        when(rootSection.getSection("controls")).thenReturn(controlsSection);
    }

    @Test
    public void createMakesItemControlsWithThrowFunction() {
        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getString("material")).thenReturn("SHEARS");

        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);
        when(rootSection.getString("throwing.throw-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("throw")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"throw\" is not a valid action type!", exception.getMessage());
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowItemTemplateConfigurationDoesNotExist() {
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getSection("item.throw-item")).thenReturn(null);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: cannot create throw function without a throw item template!", exception.getMessage());
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowItemMaterialIsInvalid() {
        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getString("material")).thenReturn("fail");

        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for the throw item material is not a valid material type!", exception.getMessage());
    }

    @Test
    public void createMakesItemControlsWithCookFunction() {
        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getString("material")).thenReturn("SHEARS");

        when(controlsSection.getString("cook")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenCookActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("cook")).thenReturn("fail");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"cook\" is not a valid action type!", exception.getMessage());
    }

    @Test
    public void createMakesItemControlsWithPlaceFunction() {
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");
        when(rootSection.getString("placing.material")).thenReturn("WARPED_BUTTON");

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenPlaceActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("place")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"place\" is not a valid action type!", exception.getMessage());
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenPlacingMaterialConfigurationValueIsInvalid() {
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");
        when(rootSection.getString("placing.material")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for the place material is not a valid material type!", exception.getMessage());
    }

    @Test
    public void createMakesItemControlsWithActivateFunction() {
        when(controlsSection.getString("activate")).thenReturn("RIGHT_CLICK");

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenActivateActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("activate")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"activate\" is not a valid action type!", exception.getMessage());
    }
}
