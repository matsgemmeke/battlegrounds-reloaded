package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.*;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class EquipmentControlsFactoryTest {

    private static final boolean ACTIVATE_EFFECT_ON_DESTRUCTION = true;
    private static final boolean REMOVE_DEPLOYMENT_ON_DESTRUCTION = false;
    private static final boolean UNDO_EFFECT_ON_DESTRUCTION = false;
    private static final boolean REMOVE_DEPLOYMENT_ON_CLEANUP = false;
    private static final double HEALTH = 50.0;
    private static final Map<String, Double> RESISTANCES = Map.of("bullet-damage", 0.5);

    private Equipment equipment;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private ProjectileEffectFactory projectileEffectFactory;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofOpenMode();
        projectileEffectFactory = mock(ProjectileEffectFactory.class);

        equipment = mock(Equipment.class);
        when(equipment.getName()).thenReturn("test equipment");
    }

    @Test
    public void createMakesItemControlsWithThrowFunction() {
        double velocity = 1.5;
        long cooldown = 20L;
        ThrowPropertiesSpec throwPropertiesSpec = new ThrowPropertiesSpec(null, velocity, cooldown);

        DeploymentSpec deploymentSpec = new DeploymentSpec(HEALTH, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, null, RESISTANCES, throwPropertiesSpec, null, null, null);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", null, null, null);
        ProjectileEffectSpec projectileEffectSpec = new ProjectileEffectSpec("BOUNCE", null, null, null, null, null, null);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(controlsSpec, deploymentSpec, List.of(projectileEffectSpec));

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionHasValueButThrowItemTemplateIsNull() {
        DeploymentSpec deploymentSpec = new DeploymentSpec(HEALTH, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, null, RESISTANCES, null, null, null, null);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", null, null, null);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(controlsSpec, deploymentSpec);

        when(equipment.getThrowItemTemplate()).thenReturn(null);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);

        assertThatThrownBy(() -> factory.create(equipmentSpec, equipment, gameKey))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw item template");
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionHasValueButThrowPropertiesIsNull() {
        DeploymentSpec deploymentSpec = new DeploymentSpec(HEALTH, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, null, RESISTANCES, null, null, null, null);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", null, null, null);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(controlsSpec, deploymentSpec);

        ItemTemplate throwItemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(throwItemTemplate);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);

        assertThatThrownBy(() -> factory.create(equipmentSpec, equipment, gameKey))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw properties");
    }

    @Test
    public void createMakesItemControlsWithCookFunction() {
        double velocity = 1.5;
        long cooldown = 20L;
        ThrowPropertiesSpec throwPropertiesSpec = new ThrowPropertiesSpec(null, velocity, cooldown);

        CookPropertiesSpec cookPropertiesSpec = new CookPropertiesSpec(null);

        DeploymentSpec deploymentSpec = new DeploymentSpec(HEALTH, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, null, RESISTANCES, throwPropertiesSpec, cookPropertiesSpec, null, null);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(controlsSpec, deploymentSpec);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenCookActionHasValueButCookPropertiesIsNull() {
        DeploymentSpec deploymentSpec = new DeploymentSpec(HEALTH, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, null, RESISTANCES, null, null, null, null);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(controlsSpec, deploymentSpec);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);

        assertThatThrownBy(() -> factory.create(equipmentSpec, equipment, gameKey))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'cook', the equipment specification does not contain the required properties");
    }

    @Test
    public void createMakesItemControlsWithPlaceFunction() {
        String material = "STICK";
        long cooldown = 20L;
        PlacePropertiesSpec placePropertiesSpec = new PlacePropertiesSpec(material, null, cooldown);

        DeploymentSpec deploymentSpec = new DeploymentSpec(HEALTH, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, null, RESISTANCES, null, null, placePropertiesSpec, null);
        ControlsSpec controlsSpec = new ControlsSpec(null, null, "RIGHT_CLICK", null);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(controlsSpec, deploymentSpec);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithActivateFunction() {
        DeploymentSpec deploymentSpec = new DeploymentSpec(HEALTH, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, null, RESISTANCES, null, null, null, null);
        ControlsSpec controlsSpec = new ControlsSpec(null, null, null, "RIGHT_CLICK");
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(controlsSpec, deploymentSpec);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    private EquipmentSpec createEquipmentSpec(ControlsSpec controlsSpec, DeploymentSpec deploymentSpec) {
        return this.createEquipmentSpec(controlsSpec, deploymentSpec, Collections.emptyList());
    }

    private EquipmentSpec createEquipmentSpec(ControlsSpec controlsSpec, DeploymentSpec deploymentSpec, List<ProjectileEffectSpec> projectileEffectSpecs) {
        ItemStackSpec displayItemSpec = new ItemStackSpec("STICK", "Test Item", 1);
        ItemEffectSpec itemEffectSpec = new ItemEffectSpec("MARK_SPAWN_POINT", Collections.emptyList(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        return new EquipmentSpec("TEST_EQUIPMENT", "Test Equipment", null, displayItemSpec, null, null, controlsSpec, deploymentSpec, itemEffectSpec, projectileEffectSpecs);
    }
}
