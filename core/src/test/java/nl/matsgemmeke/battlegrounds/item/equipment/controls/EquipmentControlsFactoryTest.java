package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunctionFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateProperties;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceFunctionFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceProperties;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class EquipmentControlsFactoryTest {

    private ActivateFunctionFactory activateFunctionFactory;
    private Equipment equipment;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private PlaceFunctionFactory placeFunctionFactory;
    private Section controlsSection;
    private Section rootSection;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        activateFunctionFactory = mock(ActivateFunctionFactory.class);
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();
        placeFunctionFactory = mock(PlaceFunctionFactory.class);
        controlsSection = mock(Section.class);
        taskRunner = mock(TaskRunner.class);

        equipment = mock(Equipment.class);
        when(equipment.getName()).thenReturn("test equipment");

        rootSection = mock(Section.class);
        when(rootSection.getSection("controls")).thenReturn(controlsSection);
    }

    @Test
    public void createMakesItemControlsWithThrowFunction() {
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getString("throwing.throw-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("throw")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"throw\" is not a valid action type!", exception.getMessage());
    }

    @Test
    public void createMakesItemControlsWithCookFunction() {
        when(controlsSection.getString("cook")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getString("item.throw-item.material")).thenReturn("SHEARS");

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenCookActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("cook")).thenReturn("fail");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"cook\" is not a valid action type!", exception.getMessage());
    }

    @Test
    public void createMakesItemControlsWithPlaceFunction() {
        long delayAfterPlacement = 10L;

        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");
        when(rootSection.getString("placing.material")).thenReturn("WARPED_BUTTON");
        when(rootSection.getLong("placing.delay-after-placement")).thenReturn(delayAfterPlacement);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        ItemFunction<EquipmentHolder> activateFunction = mock();
        when(placeFunctionFactory.create(any(PlaceProperties.class), eq(equipment), eq(audioEmitter))).thenReturn(activateFunction);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        ArgumentCaptor<PlaceProperties> propertiesCaptor = ArgumentCaptor.forClass(PlaceProperties.class);
        verify(placeFunctionFactory).create(propertiesCaptor.capture(), eq(equipment), eq(audioEmitter));

        PlaceProperties properties = propertiesCaptor.getValue();
        assertEquals(delayAfterPlacement, properties.delayAfterPlacement());
        assertEquals(Material.WARPED_BUTTON, properties.material());

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenPlaceActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("place")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"place\" is not a valid action type!", exception.getMessage());
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenPlacingMaterialConfigurationValueIsInvalid() {
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");
        when(rootSection.getString("placing.material")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for the place material is not a valid material type!", exception.getMessage());
    }

    @Test
    public void createMakesItemControlsWithActivateFunction() {
        long delayUntilActivation = 5L;

        when(controlsSection.getString("activate")).thenReturn("RIGHT_CLICK");
        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getLong("effect.activation.delay-until-activation")).thenReturn(delayUntilActivation);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        ItemFunction<EquipmentHolder> activateFunction = mock();
        when(activateFunctionFactory.create(any(ActivateProperties.class), eq(equipment), eq(audioEmitter))).thenReturn(activateFunction);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        ArgumentCaptor<ActivateProperties> propertiesCaptor = ArgumentCaptor.forClass(ActivateProperties.class);
        verify(activateFunctionFactory).create(propertiesCaptor.capture(), eq(equipment), eq(audioEmitter));

        ActivateProperties properties = propertiesCaptor.getValue();
        assertEquals(delayUntilActivation, properties.delayUntilActivation());

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenActivateActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("activate")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(activateFunctionFactory, contextProvider, placeFunctionFactory, taskRunner);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"activate\" is not a valid action type!", exception.getMessage());
    }
}
