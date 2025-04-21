package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.CookPropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.DeploySpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.PlacePropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ThrowPropertiesSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class EquipmentControlsFactoryTest {

    private static final boolean DESTROY_ON_ACTIVATE = true;
    private static final boolean DESTROY_ON_REMOVE = false;
    private static final boolean DESTROY_ON_RESET = false;
    private static final double HEALTH = 50.0;
    private static final Map<String, Double> RESISTANCES = Map.of("bullet-damage", 0.5);

    private Equipment equipment;
    private GameContextProvider contextProvider;
    private GameKey gameKey;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofTrainingMode();

        equipment = mock(Equipment.class);
        when(equipment.getName()).thenReturn("test equipment");
    }

    @Test
    public void createMakesItemControlsWithThrowFunction() {
        double velocity = 1.5;
        long cooldown = 20L;
        ThrowPropertiesSpec throwPropertiesSpec = new ThrowPropertiesSpec(null, velocity, cooldown);

        DeploySpec deploySpec = new DeploySpec(HEALTH, DESTROY_ON_ACTIVATE, DESTROY_ON_REMOVE, DESTROY_ON_RESET, RESISTANCES, throwPropertiesSpec, null, null);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", null, null, null);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider);
        ItemControls<EquipmentHolder> controls = factory.create(controlsSpec, deploySpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithCookFunction() {
        double velocity = 1.5;
        long cooldown = 20L;
        ThrowPropertiesSpec throwPropertiesSpec = new ThrowPropertiesSpec(null, velocity, cooldown);

        CookPropertiesSpec cookPropertiesSpec = new CookPropertiesSpec(null);

        DeploySpec deploySpec = new DeploySpec(HEALTH, DESTROY_ON_ACTIVATE, DESTROY_ON_REMOVE, DESTROY_ON_RESET, RESISTANCES, throwPropertiesSpec, cookPropertiesSpec, null);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider);
        ItemControls<EquipmentHolder> controls = factory.create(controlsSpec, deploySpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithPlaceFunction() {
        String material = "STICK";
        long cooldown = 20L;
        PlacePropertiesSpec placePropertiesSpec = new PlacePropertiesSpec(material, null, cooldown);

        DeploySpec deploySpec = new DeploySpec(HEALTH, DESTROY_ON_ACTIVATE, DESTROY_ON_REMOVE, DESTROY_ON_RESET, RESISTANCES, null, null, placePropertiesSpec);
        ControlsSpec controlsSpec = new ControlsSpec(null, null, "RIGHT_CLICK", null);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider);
        ItemControls<EquipmentHolder> controls = factory.create(controlsSpec, deploySpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithActivateFunction() {
        DeploySpec deploySpec = new DeploySpec(HEALTH, DESTROY_ON_ACTIVATE, DESTROY_ON_REMOVE, DESTROY_ON_RESET, RESISTANCES, null, null, null);
        ControlsSpec controlsSpec = new ControlsSpec(null, null, null, "RIGHT_CLICK");

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider);
        ItemControls<EquipmentHolder> controls = factory.create(controlsSpec, deploySpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }
}
