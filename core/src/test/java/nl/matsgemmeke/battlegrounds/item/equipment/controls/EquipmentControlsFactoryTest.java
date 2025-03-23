package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunctionFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateProperties;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
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
    private NamespacedKeyCreator namespacedKeyCreator;
    private Section controlsSection;
    private Section rootSection;

    @BeforeEach
    public void setUp() {
        activateFunctionFactory = mock(ActivateFunctionFactory.class);
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
        long delayAfterThrow = 30L;
        double velocity = 1.2;

        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getString("material")).thenReturn("SHEARS");

        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);
        when(rootSection.getLong("throwing.delay-after-throw")).thenReturn(delayAfterThrow);
        when(rootSection.getString("throwing.throw-sound")).thenReturn("AMBIENT_CAVE-1-1-1");
        when(rootSection.getDouble("throwing.velocity")).thenReturn(velocity);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("throw")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"throw\" is not a valid action type!", exception.getMessage());
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowItemTemplateConfigurationDoesNotExist() {
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getSection("item.throw-item")).thenReturn(null);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: cannot create throw function without a throw item template!", exception.getMessage());
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowItemMaterialIsInvalid() {
        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getString("material")).thenReturn("fail");

        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");
        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);

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

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenCookActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("cook")).thenReturn("fail");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);

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

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);
        ItemControls<EquipmentHolder> controls = factory.create(rootSection, equipment, gameKey);

        assertNotNull(controls);
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenPlaceActionConfigurationValueIsInvalid() {
        when(controlsSection.getString("place")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"place\" is not a valid action type!", exception.getMessage());
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenPlacingMaterialConfigurationValueIsInvalid() {
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");
        when(rootSection.getString("placing.material")).thenReturn("fail");

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);

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

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);
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

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, activateFunctionFactory, namespacedKeyCreator);

        Exception exception = assertThrows(EquipmentControlsCreationException.class, () -> factory.create(rootSection, equipment, gameKey));
        assertEquals("Error while creating controls for equipment test equipment: the value \"fail\" for function \"activate\" is not a valid action type!", exception.getMessage());
    }
}
