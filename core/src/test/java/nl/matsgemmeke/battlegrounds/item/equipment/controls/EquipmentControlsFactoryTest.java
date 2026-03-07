package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.drop.DropDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentControlsFactoryTest {

    @Mock
    private Equipment equipment;
    @Mock
    private ProjectileEffectFactory projectileEffectFactory;
    @Mock
    private Provider<DropDeployment> dropDeploymentProvider;
    @Mock
    private Provider<PlaceDeployment> placeDeploymentProvider;
    @Mock
    private Provider<PrimeDeployment> primeDeploymentProvider;
    @Mock
    private Provider<ThrowDeployment> throwDeploymentProvider;

    private EquipmentControlsFactory controlsFactory;

    @BeforeEach
    void setUp() {
        controlsFactory = new EquipmentControlsFactory(projectileEffectFactory, dropDeploymentProvider, placeDeploymentProvider, primeDeploymentProvider, throwDeploymentProvider);
    }

    @Test
    @DisplayName("create returns ItemControls with throw function")
    void create_withThrowFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");
        ThrowDeployment throwDeployment = mock(ThrowDeployment.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        when(throwDeploymentProvider.get()).thenReturn(throwDeployment);

        ItemControls<EquipmentHolder> controls = controlsFactory.create(equipmentSpec, equipment);

        assertThat(controls).isNotNull();
    }

    @Test
    @DisplayName("create throws EquipmentControlsCreationException when throw action has value but throw item template is null")
    void create_throwItemTemplateNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");

        when(equipment.getThrowItemTemplate()).thenReturn(null);

        assertThatThrownBy(() -> controlsFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw item template");
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
    @DisplayName("create returns ItemControls with cook function")
    void create_withCookFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        PrimeDeployment primeDeployment = mock(PrimeDeployment.class);
        ThrowDeployment throwDeployment = mock(ThrowDeployment.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        when(primeDeploymentProvider.get()).thenReturn(primeDeployment);
        when(throwDeploymentProvider.get()).thenReturn(throwDeployment);

        ItemControls<EquipmentHolder> controls = controlsFactory.create(equipmentSpec, equipment);

        assertThat(controls).isNotNull();
    }

    @Test
    @DisplayName("create returns ItemControls with place function")
    void create_withPlaceFunction() {
        PlaceDeployment placeDeployment = mock(PlaceDeployment.class);

        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.activate = null;
        equipmentSpec.controls.throwing = null;

        when(placeDeploymentProvider.get()).thenReturn(placeDeployment);

        ItemControls<EquipmentHolder> controls = controlsFactory.create(equipmentSpec, equipment);

        assertThat(controls).isNotNull();
    }

    @Test
    @DisplayName("create returns ItemControls with activate function")
    void create_withActivateFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.place = null;
        equipmentSpec.controls.throwing = null;

        ItemControls<EquipmentHolder> controls = controlsFactory.create(equipmentSpec, equipment);

        assertThat(controls).isNotNull();
    }

    private EquipmentSpec createEquipmentSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }
}
