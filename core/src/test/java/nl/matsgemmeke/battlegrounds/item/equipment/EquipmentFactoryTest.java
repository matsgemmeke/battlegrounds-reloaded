package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentFactoryTest {

    private static final String TEMPLATE_ID_KEY = "template-id";

    @Mock
    private DeploymentHandlerFactory deploymentHandlerFactory;
    @Mock
    private EquipmentControlsFactory controlsFactory;
    @Mock
    private EquipmentRegistry equipmentRegistry;
    @Mock
    private ItemEffectFactory itemEffectFactory;
    @Mock
    private ItemFactory itemFactory;
    @Mock
    private NamespacedKeyCreator keyCreator;
    @Spy
    private ParticleEffectMapper particleEffectMapper = new ParticleEffectMapper();
    @Mock
    private TriggerExecutorFactory triggerExecutorFactory;
    @InjectMocks
    private EquipmentFactory equipmentFactory;

    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    void setUp() {
        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, TEMPLATE_ID_KEY);
        when(keyCreator.create(TEMPLATE_ID_KEY)).thenReturn(key);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    void createReturnsEquipmentWithPlayerHolder() {
        EquipmentSpec spec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class))).thenReturn(controls);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(itemEffectFactory.create(spec.effect)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(itemEffect))).thenReturn(deploymentHandler);

        Equipment equipment = equipmentFactory.create(spec, gamePlayer);

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getName()).isEqualTo("Frag Grenade");

        verify(equipmentRegistry).register(equipment, gamePlayer);
    }

    @Test
    void createMakesEquipmentWithActivator() {
        EquipmentSpec spec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class))).thenReturn(controls);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(itemEffectFactory.create(spec.effect)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(itemEffect))).thenReturn(deploymentHandler);

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
