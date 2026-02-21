package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
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
    private DeploymentHandlerFactory deploymentHandlerFactory;
    @Mock
    private EquipmentControlsFactory controlsFactory;
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
    @DisplayName("create returns Equipment instance with player holder")
    void create_withPlayerHolder() {
        EquipmentSpec spec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        ItemEffect itemEffect = mock(ItemEffect.class);
        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(ITEM_STACK_DISPLAY);

        when(controlsFactory.create(eq(spec), any(Equipment.class))).thenReturn(controls);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(itemEffect))).thenReturn(deploymentHandler);
        when(itemEffectFactory.create(spec.effect)).thenReturn(itemEffect);
        when(itemTemplateFactory.create(spec.items.displayItem, "equipment")).thenReturn(displayItemTemplate);
        when(triggerExecutorFactory.create(spec.deploy.triggers.get("scheduled"))).thenReturn(triggerExecutor);

        Equipment equipment = equipmentFactory.create(spec, gamePlayer);

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getName()).isEqualTo("Frag Grenade");
        assertThat(equipment.getItemStack()).isEqualTo(ITEM_STACK_DISPLAY);

        verify(equipmentRegistry).register(equipment, gamePlayer);
        verify(deploymentHandler).addTriggerExecutor(triggerExecutor);
    }

    @Test
    @DisplayName("create returns Equipment instance with activator")
    void create_withActivator() {
        EquipmentSpec spec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");
        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        ItemEffect itemEffect = mock(ItemEffect.class);
        ItemTemplate activatorItemTemplate = mock(ItemTemplate.class);

        ItemTemplate displayItemTemplate = mock(ItemTemplate.class);
        when(displayItemTemplate.createItemStack(any())).thenReturn(ITEM_STACK_DISPLAY);

        when(controlsFactory.create(eq(spec), any(Equipment.class))).thenReturn(controls);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(itemEffect))).thenReturn(deploymentHandler);
        when(itemEffectFactory.create(spec.effect)).thenReturn(itemEffect);
        when(itemTemplateFactory.create(spec.items.displayItem, "equipment")).thenReturn(displayItemTemplate);
        when(itemTemplateFactory.create(spec.items.activatorItem, "equipment")).thenReturn(activatorItemTemplate);

        Equipment equipment = equipmentFactory.create(spec);

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
