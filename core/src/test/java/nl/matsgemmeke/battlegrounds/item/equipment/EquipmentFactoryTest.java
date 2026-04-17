package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.deploy.state.DeploymentState;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControllerFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentFactoryTest {

    private static final ItemStack ITEM_STACK_DISPLAY = new ItemStack(Material.STICK);
    private static final ItemStack ITEM_STACK_ACTIVATOR = new ItemStack(Material.FEATHER);

    @Mock
    private DeploymentFactory deploymentFactory;
    @Mock
    private EquipmentControllerFactory controllerFactory;
    @Mock
    private EquipmentRegistry equipmentRegistry;
    @Mock
    private ItemEffectFactory itemEffectFactory;
    @Mock
    private ItemTemplateFactory itemTemplateFactory;
    @Spy
    private ParticleEffectMapper particleEffectMapper;
    @Mock
    private TriggerExecutorFactory triggerExecutorFactory;
    @InjectMocks
    private EquipmentFactory equipmentFactory;

    @Test
    @DisplayName("create returns Equipment instance with player user")
    void create_withPlayerUser() {
        EquipmentSpec spec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemController<EquipmentUser> controller = new ItemController<>();
        Deployment deployment = mock(Deployment.class);
        ItemEffect itemEffect = mock(ItemEffect.class);
        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(ITEM_STACK_DISPLAY);

        when(controllerFactory.create(eq(spec), any(Equipment.class))).thenReturn(controller);
        when(deploymentFactory.create(any(DeploymentProperties.class), any(DeploymentState.class), eq(itemEffect))).thenReturn(deployment);
        when(itemEffectFactory.create(spec.effect)).thenReturn(itemEffect);
        when(itemTemplateFactory.create(spec.items.displayItem)).thenReturn(displayItemTemplate);
        when(triggerExecutorFactory.create(spec.deploy.triggers.get("scheduled"))).thenReturn(triggerExecutor);

        Equipment equipment = equipmentFactory.create(spec, gamePlayer);

        ArgumentCaptor<DeploymentProperties> deploymentPropertiesCaptor = ArgumentCaptor.forClass(DeploymentProperties.class);
        verify(deploymentFactory).create(deploymentPropertiesCaptor.capture(), any(DeploymentState.class), any(ItemEffect.class));

        assertThat(deploymentPropertiesCaptor.getValue()).satisfies(properties -> {
           assertThat(properties.activateEffectOnDestruction()).isTrue();
           assertThat(properties.destructionParticleEffect()).isNull();
           assertThat(properties.manualActivationDelay()).isZero();
           assertThat(properties.manualActivationSounds()).isEmpty();
           assertThat(properties.removeDeploymentOnCleanup()).isFalse();
           assertThat(properties.removeDeploymentOnDestruction()).isTrue();
           assertThat(properties.undoEffectOnDestruction()).isFalse();
        });

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getName()).isEqualTo("Frag Grenade");
        assertThat(equipment.getItemStack()).isEqualTo(ITEM_STACK_DISPLAY);

        verify(equipmentRegistry).register(equipment, gamePlayer);
        verify(deployment).addTriggerExecutor(triggerExecutor);
    }

    @Test
    @DisplayName("create returns Equipment instance with activator")
    void create_withActivator() {
        EquipmentSpec spec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        ItemController<EquipmentUser> controller = new ItemController<>();
        Deployment deployment = mock(Deployment.class);
        ItemEffect itemEffect = mock(ItemEffect.class);
        ItemTemplate activatorItemTemplate = mock(ItemTemplate.class);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(ITEM_STACK_DISPLAY);

        when(controllerFactory.create(eq(spec), any(Equipment.class))).thenReturn(controller);
        when(deploymentFactory.create(any(DeploymentProperties.class), any(DeploymentState.class), eq(itemEffect))).thenReturn(deployment);
        when(itemEffectFactory.create(spec.effect)).thenReturn(itemEffect);
        when(itemTemplateFactory.create(spec.items.displayItem)).thenReturn(displayItemTemplate);
        when(itemTemplateFactory.create(spec.items.activatorItem)).thenReturn(activatorItemTemplate);

        Equipment equipment = equipmentFactory.create(spec);

        ArgumentCaptor<DeploymentProperties> deploymentPropertiesCaptor = ArgumentCaptor.forClass(DeploymentProperties.class);
        verify(deploymentFactory).create(deploymentPropertiesCaptor.capture(), any(DeploymentState.class), any(ItemEffect.class));

        assertThat(deploymentPropertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.activateEffectOnDestruction()).isFalse();
            assertThat(properties.destructionParticleEffect()).isNull();
            assertThat(properties.manualActivationDelay()).isEqualTo(5);
            assertThat(properties.manualActivationSounds()).isNotEmpty();
            assertThat(properties.removeDeploymentOnCleanup()).isTrue();
            assertThat(properties.removeDeploymentOnDestruction()).isTrue();
            assertThat(properties.undoEffectOnDestruction()).isFalse();
        });

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getActivator()).isNotNull();
        assertThat(equipment.getActivator()).isInstanceOf(DefaultActivator.class);

        verify(equipmentRegistry).register(equipment);
    }

    private EquipmentSpec createEquipmentSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }
}
