package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeployment;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class EquipmentControlsFactoryTest {

    private Equipment equipment;
    private ProjectileEffectFactory projectileEffectFactory;
    private Provider<PlaceDeployment> placeDeploymentProvider;
    private Provider<PrimeDeployment> primeDeploymentProvider;
    private Provider<ThrowDeployment> throwDeploymentProvider;

    @BeforeEach
    public void setUp() {
        projectileEffectFactory = mock(ProjectileEffectFactory.class);
        placeDeploymentProvider = mock();
        primeDeploymentProvider = mock();
        throwDeploymentProvider = mock();

        equipment = mock(Equipment.class);
        when(equipment.getName()).thenReturn("test equipment");
    }

    @Test
    public void createMakesItemControlsWithThrowFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");
        ThrowDeployment throwDeployment = mock(ThrowDeployment.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        when(throwDeploymentProvider.get()).thenReturn(throwDeployment);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(projectileEffectFactory, placeDeploymentProvider, primeDeploymentProvider, throwDeploymentProvider);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionHasValueButThrowItemTemplateIsNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");

        when(equipment.getThrowItemTemplate()).thenReturn(null);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(projectileEffectFactory, placeDeploymentProvider, primeDeploymentProvider, throwDeploymentProvider);

        assertThatThrownBy(() -> factory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw item template");
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionHasValueButThrowPropertiesIsNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");
        equipmentSpec.deploy.throwing = null;

        ItemTemplate throwItemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(throwItemTemplate);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(projectileEffectFactory, placeDeploymentProvider, primeDeploymentProvider, throwDeploymentProvider);

        assertThatThrownBy(() -> factory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw properties");
    }

    @Test
    public void createMakesItemControlsWithCookFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        PrimeDeployment primeDeployment = mock(PrimeDeployment.class);
        ThrowDeployment throwDeployment = mock(ThrowDeployment.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        when(primeDeploymentProvider.get()).thenReturn(primeDeployment);
        when(throwDeploymentProvider.get()).thenReturn(throwDeployment);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(projectileEffectFactory, placeDeploymentProvider, primeDeploymentProvider, throwDeploymentProvider);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithPlaceFunction() {
        PlaceDeployment placeDeployment = mock(PlaceDeployment.class);

        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.activate = null;
        equipmentSpec.controls.throwing = null;

        when(placeDeploymentProvider.get()).thenReturn(placeDeployment);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(projectileEffectFactory, placeDeploymentProvider, primeDeploymentProvider, throwDeploymentProvider);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithActivateFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.place = null;
        equipmentSpec.controls.throwing = null;

        EquipmentControlsFactory factory = new EquipmentControlsFactory(projectileEffectFactory, placeDeploymentProvider, primeDeploymentProvider, throwDeploymentProvider);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment);

        assertThat(controls).isNotNull();
    }

    private EquipmentSpec createEquipmentSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }
}
