package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.deploy.action.*;
import nl.matsgemmeke.battlegrounds.item.equipment.*;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.cook.CookFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.api.SoundService;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentControllerFactoryTest {

    private static final UUID EQUIPMENT_ID = UUID.randomUUID();

    @Spy
    private ActionBindingMapper actionBindingMapper;
    @Mock
    private Equipment equipment;
    @Mock
    private ItemController<EquipmentUser> controller;
    @Mock
    private ItemControllerRegistry itemControllerRegistry;
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
    private SoundService soundService;
    @Mock
    private Supplier<ItemController<EquipmentUser>> controllerSupplier;
    @Captor
    private ArgumentCaptor<ActionBinding<EquipmentUser>> bindingCaptor;

    private EquipmentControllerFactory controllerFactory;

    @BeforeEach
    void setUp() {
        when(controllerSupplier.get()).thenReturn(controller);

        controllerFactory = new EquipmentControllerFactory(actionBindingMapper, itemControllerRegistry, itemTemplateFactory, projectileEffectFactory, dropDeploymentActionProvider, placeDeploymentActionProvider, primeDeploymentActionProvider, throwDeploymentActionProvider, soundService, controllerSupplier);
    }

    @Test
    @DisplayName("create throws EquipmentControllerCreationException when throw action has value but throw properties is null")
    void create_throwPropertiesNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");
        equipmentSpec.deploy.throwing = null;

        assertThatThrownBy(() -> controllerFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControllerCreationException.class)
                .hasMessage("Cannot create controller for 'throw', the equipment specification does not contain the required throw properties");
    }

    @Test
    @DisplayName("create throws EquipmentControllerCreationException when throwing sound sequence is invalid")
    void create_invalidThrowingSoundSequence() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");

        when(soundService.getSoundSequence("equipment-throw-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controllerFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControllerCreationException.class)
                .hasMessage("Throw sound sequence 'equipment-throw-1' does not exist");
    }

    @Test
    @DisplayName("create returns ItemController with throw function")
    void create_withThrowFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/semtex.yml");
        ThrowDeploymentAction deploymentAction = mock(ThrowDeploymentAction.class);
        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        SoundSequence soundSequence = mock(SoundSequence.class);

        when(itemTemplateFactory.create(equipmentSpec.items.throwItem)).thenReturn(itemTemplate);
        when(soundService.getSoundSequence("equipment-throw-1")).thenReturn(Optional.of(soundSequence));
        when(throwDeploymentActionProvider.get()).thenReturn(deploymentAction);
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);

        ItemController<EquipmentUser> controller = controllerFactory.create(equipmentSpec, equipment);

        ArgumentCaptor<ThrowDeploymentProperties> propertiesCaptor = ArgumentCaptor.forClass(ThrowDeploymentProperties.class);
        verify(deploymentAction).configureProperties(propertiesCaptor.capture());

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.itemTemplate()).isEqualTo(itemTemplate);
            assertThat(properties.soundSequence()).isEqualTo(soundSequence);
            assertThat(properties.resistances()).containsOnly(
                    entry(DamageType.BULLET_DAMAGE, 0.0),
                    entry(DamageType.EXPLOSIVE_DAMAGE, 0.0),
                    entry(DamageType.FIRE_DAMAGE, 0.0)
            );
            assertThat(properties.health()).isEqualTo(50.0);
            assertThat(properties.velocity()).isEqualTo(1.2);
            assertThat(properties.cooldown()).isEqualTo(30L);
        });

        verify(controller).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(ThrowFunction.class);
            assertThat(binding.blocking()).isTrue();
            assertThat(binding.cancelsEvent()).isTrue();
        });

        verify(itemControllerRegistry).registerEquipmentController(EQUIPMENT_ID, controller);
    }

    @Test
    @DisplayName("create throws EquipmentControllerCreationException when place action has value but place properties is null")
    void create_placePropertiesNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.activate = null;
        equipmentSpec.controls.throwing = null;
        equipmentSpec.deploy.placing = null;

        assertThatThrownBy(() -> controllerFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControllerCreationException.class)
                .hasMessage("Cannot create controller for 'place', the equipment specification does not contain the required place properties");
    }

    @Test
    @DisplayName("create throws EquipmentControllerCreationException when placing sound sequence is invalid")
    void create_invalidPlacingSoundSequence() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.activate = null;
        equipmentSpec.controls.throwing = null;

        when(soundService.getSoundSequence("equipment-place-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controllerFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControllerCreationException.class)
                .hasMessage("Place sound sequence 'equipment-place-1' does not exist");
    }

    @Test
    @DisplayName("create returns ItemController with place function")
    void create_withPlaceFunction() {
        PlaceDeploymentAction deploymentAction = mock(PlaceDeploymentAction.class);
        SoundSequence soundSequence = mock(SoundSequence.class);

        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.activate = null;
        equipmentSpec.controls.throwing = null;

        when(soundService.getSoundSequence("equipment-place-1")).thenReturn(Optional.of(soundSequence));
        when(placeDeploymentActionProvider.get()).thenReturn(deploymentAction);
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);

        ItemController<EquipmentUser> controller = controllerFactory.create(equipmentSpec, equipment);

        ArgumentCaptor<PlaceDeploymentProperties> placeDeploymentPropertiesCaptor = ArgumentCaptor.forClass(PlaceDeploymentProperties.class);
        verify(deploymentAction).configureProperties(placeDeploymentPropertiesCaptor.capture());

        assertThat(placeDeploymentPropertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.resistances()).containsOnly(
                    entry(DamageType.BULLET_DAMAGE, 0.0),
                    entry(DamageType.EXPLOSIVE_DAMAGE, 0.0),
                    entry(DamageType.FIRE_DAMAGE, 0.0)
            );
            assertThat(properties.material()).isEqualTo(Material.WARPED_BUTTON);
            assertThat(properties.soundSequence()).isEqualTo(soundSequence);
            assertThat(properties.health()).isEqualTo(50.0);
            assertThat(properties.cooldown()).isEqualTo(10L);
        });

        verify(controller).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(PlaceFunction.class);
            assertThat(binding.blocking()).isTrue();
            assertThat(binding.cancelsEvent()).isTrue();
        });

        verify(itemControllerRegistry).registerEquipmentController(EQUIPMENT_ID, controller);
    }

    @Test
    @DisplayName("create throws EquipmentControllerCreationException when cook action has value but cook properties is null")
    void create_cookPropertiesNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;
        equipmentSpec.deploy.cooking = null;

        assertThatThrownBy(() -> controllerFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControllerCreationException.class)
                .hasMessage("Cannot create controller for 'cook', the equipment specification does not contain the required cook properties");
    }

    @Test
    @DisplayName("create throws EquipmentControllerCreationException when cook sound sequence is invalid")
    void create_invalidCookingSoundSequence() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;

        when(soundService.getSoundSequence("equipment-prime-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controllerFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControllerCreationException.class)
                .hasMessage("Cook sound sequence 'equipment-prime-1' does not exist");
    }

    @Test
    @DisplayName("create returns ItemController with cook function")
    void create_withCookFunction() {
        SoundSequence soundSequence = mock(SoundSequence.class);
        PrimeDeploymentAction deploymentAction = mock(PrimeDeploymentAction.class);

        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;
        equipmentSpec.controls.drop = null;

        when(soundService.getSoundSequence("equipment-prime-1")).thenReturn(Optional.of(soundSequence));
        when(primeDeploymentActionProvider.get()).thenReturn(deploymentAction);
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);

        ItemController<EquipmentUser> controller = controllerFactory.create(equipmentSpec, equipment);

        verify(controller).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(CookFunction.class);
            assertThat(binding.blocking()).isFalse();
            assertThat(binding.cancelsEvent()).isTrue();
        });

        verify(itemControllerRegistry).registerEquipmentController(EQUIPMENT_ID, controller);
        verify(deploymentAction).configureSoundSequence(soundSequence);
    }

    @Test
    @DisplayName("create throws EquipmentControllerCreationException when drop action has value but drop properties is null")
    void create_dropPropertiesNull() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;
        equipmentSpec.controls.cook = null;
        equipmentSpec.deploy.dropping = null;

        assertThatThrownBy(() -> controllerFactory.create(equipmentSpec, equipment))
                .isInstanceOf(EquipmentControllerCreationException.class)
                .hasMessage("Cannot create controller for 'drop', the equipment specification does not contain the required drop properties");
    }

    @Test
    @DisplayName("create returns ItemController with drop function")
    void create_withDropFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        equipmentSpec.controls.throwing = null;
        equipmentSpec.controls.cook = null;

        DropDeploymentAction deploymentAction = mock(DropDeploymentAction.class);
        ItemTemplate itemTemplate = mock(ItemTemplate.class);

        when(dropDeploymentActionProvider.get()).thenReturn(deploymentAction);
        when(itemTemplateFactory.create(equipmentSpec.items.dropItem)).thenReturn(itemTemplate);
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);

        ItemController<EquipmentUser> controller = controllerFactory.create(equipmentSpec, equipment);

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

        verify(controller).bind(eq(Action.DROP_ITEM), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(DropFunction.class);
            assertThat(binding.blocking()).isTrue();
            assertThat(binding.cancelsEvent()).isTrue();
        });

        verify(itemControllerRegistry).registerEquipmentController(EQUIPMENT_ID, controller);
    }

    @Test
    @DisplayName("create returns ItemController with activate function")
    void create_withActivateFunction() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        equipmentSpec.controls.place = null;
        equipmentSpec.controls.throwing = null;

        when(equipment.getId()).thenReturn(EQUIPMENT_ID);

        ItemController<EquipmentUser> controller = controllerFactory.create(equipmentSpec, equipment);

        verify(controller).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(ActivateFunction.class);
            assertThat(binding.blocking()).isTrue();
            assertThat(binding.cancelsEvent()).isTrue();
        });

        verify(itemControllerRegistry).registerEquipmentController(EQUIPMENT_ID, controller);
    }

    private EquipmentSpec createEquipmentSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }
}
