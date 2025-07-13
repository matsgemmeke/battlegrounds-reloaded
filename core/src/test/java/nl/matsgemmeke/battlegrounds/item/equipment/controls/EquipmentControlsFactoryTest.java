package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
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
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");

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
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");

        when(equipment.getThrowItemTemplate()).thenReturn(null);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);

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

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);

        assertThatThrownBy(() -> factory.create(equipmentSpec, equipment, gameKey))
                .isInstanceOf(EquipmentControlsCreationException.class)
                .hasMessage("Cannot create controls for 'throw', the equipment specification does not contain the required throw properties");
    }

    @Test
    public void createMakesItemControlsWithCookFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(equipment.getThrowItemTemplate()).thenReturn(itemTemplate);

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);
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

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);
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

        EquipmentControlsFactory factory = new EquipmentControlsFactory(contextProvider, projectileEffectFactory);
        ItemControls<EquipmentHolder> controls = factory.create(equipmentSpec, equipment, gameKey);

        assertThat(controls).isNotNull();
    }

    private EquipmentSpec createEquipmentSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }
}
