package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.action.*;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.cook.CookFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentControlsFactoryTest {

    @Mock
    private Equipment equipment;
    @Mock
    private ItemControls<EquipmentUser> controls;
    @Mock
    private ItemTemplateFactory itemTemplateFactory;
    @Mock
    private ProjectileEffectFactory projectileEffectFactory;
    @Mock
    private Provider<DropDeploymentAction> dropDeploymentActionProvider;
    @Mock
    private Provider<PlaceDeploymentAction> placeDeploymentActionProvider;
    @Mock
    private Provider<PrimeDeploymentAction> primeDeploymentActionProvider;
    @Mock
    private Provider<ThrowDeploymentAction> throwDeploymentActionProvider;
    @Mock
    private Supplier<ItemControls<EquipmentUser>> controlsSupplier;

    private EquipmentControlsFactory controlsFactory;

    @BeforeEach
    void setUp() {
        when(controlsSupplier.get()).thenReturn(controls);

        controlsFactory = new EquipmentControlsFactory(itemTemplateFactory, projectileEffectFactory, dropDeploymentActionProvider, placeDeploymentActionProvider, primeDeploymentActionProvider, throwDeploymentActionProvider, controlsSupplier);
    }

    @Test
    @DisplayName("create throws EquipmentControlsCreationException when throw action has value but throw properties is null")
    void create_throwPropertiesNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");
        equipmentSpec.deploy.throwing = null;

        assertThatThrownBy(() -> controlsFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw properties");
    }

    @Test
    @DisplayName("create returns ItemControls with throw function")
    void create_withThrowFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");
        ThrowDeploymentAction deploymentAction = mock(ThrowDeploymentAction.class);
        ItemTemplate itemTemplate = mock(ItemTemplate.class);

        when(itemTemplateFactory.create(equipmentSpec.items.throwItem)).thenReturn(itemTemplate);
        when(throwDeploymentActionProvider.get()).thenReturn(deploymentAction);

        ItemControls<EquipmentUser> controls = controlsFactory.create(equipmentSpec, equipment);

        ArgumentCaptor<ThrowDeploymentProperties> propertiesCaptor = ArgumentCaptor.forClass(ThrowDeploymentProperties.class);
        verify(deploymentAction).configureProperties(propertiesCaptor.capture());

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.itemTemplate()).isEqualTo(itemTemplate);
            assertThat(properties.throwSounds()).isNotEmpty();
            assertThat(properties.resistances()).containsOnly(
                    entry(DamageType.BULLET_DAMAGE, 0.0),
                    entry(DamageType.EXPLOSIVE_DAMAGE, 0.0),
                    entry(DamageType.FIRE_DAMAGE, 0.0)
            );
            assertThat(properties.health()).isEqualTo(50.0);
            assertThat(properties.velocity()).isEqualTo(1.2);
            assertThat(properties.cooldown()).isEqualTo(30L);
        });

        verify(controls).addControl(eq(Action.LEFT_CLICK), any(ThrowFunction.class));
    }

    @Test
    @DisplayName("create throws EquipmentControlsCreationException when place action has value but place properties is null")
    void create_placePropertiesNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.activate = null;
        equipmentSpec.controls.throwing = null;
        equipmentSpec.deploy.placing = null;

        assertThatThrownBy(() -> controlsFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'place', the equipment specification does not contain the required place properties");
    }

    @Test
    @DisplayName("create returns ItemControls with place function")
    void create_withPlaceFunction() {
        PlaceDeploymentAction deploymentAction = mock(PlaceDeploymentAction.class);

        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.activate = null;
        equipmentSpec.controls.throwing = null;

        when(placeDeploymentActionProvider.get()).thenReturn(deploymentAction);

        ItemControls<EquipmentUser> controls = controlsFactory.create(equipmentSpec, equipment);

        verify(controls).addControl(eq(Action.RIGHT_CLICK), any(PlaceFunction.class));
    }

    @Test
    @DisplayName("create throws EquipmentControlsCreationException when cook action has value but cook properties is null")
    void create_cookPropertiesNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;
        equipmentSpec.deploy.cooking = null;

        assertThatThrownBy(() -> controlsFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'cook', the equipment specification does not contain the required cook properties");
    }

    @Test
    @DisplayName("create returns ItemControls with cook function")
    void create_withCookFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;
        equipmentSpec.controls.drop = null;

        PrimeDeploymentAction deploymentAction = mock(PrimeDeploymentAction.class);

        when(primeDeploymentActionProvider.get()).thenReturn(deploymentAction);

        ItemControls<EquipmentUser> controls = controlsFactory.create(equipmentSpec, equipment);

        verify(controls).addControl(eq(Action.RIGHT_CLICK), any(CookFunction.class));
    }

    @Test
    @DisplayName("create throws EquipmentControlsCreationException when drop action has value but drop properties is null")
    void create_dropPropertiesNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;
        equipmentSpec.controls.cook = null;
        equipmentSpec.deploy.dropping = null;

        assertThatThrownBy(() -> controlsFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'drop', the equipment specification does not contain the required drop properties");
    }

    @Test
    @DisplayName("create returns ItemControls with drop function")
    void create_withDropFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;
        equipmentSpec.controls.cook = null;

        DropDeploymentAction deploymentAction = mock(DropDeploymentAction.class);
        ItemTemplate itemTemplate = mock(ItemTemplate.class);

        when(dropDeploymentActionProvider.get()).thenReturn(deploymentAction);
        when(itemTemplateFactory.create(equipmentSpec.items.dropItem)).thenReturn(itemTemplate);

        ItemControls<EquipmentUser> controls = controlsFactory.create(equipmentSpec, equipment);

        ArgumentCaptor<DropDeploymentProperties> propertiesCaptor = ArgumentCaptor.forClass(DropDeploymentProperties.class);
        verify(deploymentAction).configureProperties(propertiesCaptor.capture());

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.itemTemplate()).isEqualTo(itemTemplate);
            assertThat(properties.resistances()).containsOnly(
                    entry(DamageType.BULLET_DAMAGE, 0.0),
                    entry(DamageType.FIRE_DAMAGE, 0.0)
            );
            assertThat(properties.health()).isEqualTo(50.0);
            assertThat(properties.velocity()).isEqualTo(1.0);
            assertThat(properties.cooldown()).isEqualTo(30L);
        });

        verify(controls).addControl(eq(Action.DROP_ITEM), any(DropFunction.class));
    }

    @Test
    @DisplayName("create returns ItemControls with activate function")
    void create_withActivateFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.place = null;
        equipmentSpec.controls.throwing = null;

        ItemControls<EquipmentUser> controls = controlsFactory.create(equipmentSpec, equipment);

        verify(controls).addControl(eq(Action.RIGHT_CLICK), any(ActivateFunction.class));
    }

    private EquipmentSpec createEquipmentSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }
}
