package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeployment;
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
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private ProjectileEffectFactory projectileEffectFactory;
    private Provider<PrimeDeployment> primeDeploymentProvider;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
        gameKey = GameKey.ofOpenMode();
        projectileEffectFactory = mock(ProjectileEffectFactory.class);
        primeDeploymentProvider = mock();

        equipment = mock(Equipment.class);
        when(equipment.getName()).thenReturn("test equipment");
    }

    @Test
    public void createMakesItemControlsWithThrowFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory, primeDeploymentProvider);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionHasValueButThrowItemTemplateIsNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");

        when(equipment.getThrowItemTemplate()).thenReturn(null);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory, primeDeploymentProvider);

        assertThatThrownBy(() -> factory.create(equipmentSpec, equipment, gameKey))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw item template");
    }

    @Test
    public void createThrowsEquipmentControlsCreationExceptionWhenThrowActionHasValueButThrowPropertiesIsNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");
        equipmentSpec.deploy.throwing = null;

        ItemTemplate throwItemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(throwItemTemplate);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory, primeDeploymentProvider);

        assertThatThrownBy(() -> factory.create(equipmentSpec, equipment, gameKey))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw properties");
    }

    @Test
    public void createMakesItemControlsWithCookFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        PrimeDeployment primeDeployment = mock(PrimeDeployment.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        when(primeDeploymentProvider.get()).thenReturn(primeDeployment);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory, primeDeploymentProvider);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithPlaceFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.activate = null;
        equipmentSpec.controls.throwing = null;

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory, primeDeploymentProvider);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    @Test
    public void createMakesItemControlsWithActivateFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.place = null;
        equipmentSpec.controls.throwing = null;

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory, primeDeploymentProvider);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    private EquipmentSpec createEquipmentSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }
}
