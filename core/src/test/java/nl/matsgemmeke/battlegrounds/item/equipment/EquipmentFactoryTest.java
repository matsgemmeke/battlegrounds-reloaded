package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandler;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.effect.DefaultActivator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.EquipmentControlsFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EquipmentFactoryTest {

    private static final String TEMPLATE_ID_KEY = "template-id";

    private DeploymentHandlerFactory deploymentHandlerFactory;
    private EquipmentControlsFactory controlsFactory;
    private EquipmentRegistry equipmentRegistry;
    private GameKey gameKey;
    private ItemEffectFactory itemEffectFactory;
    private ItemFactory itemFactory;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private ParticleEffectMapper particleEffectMapper;

    @BeforeEach
    public void setUp() {
        deploymentHandlerFactory = mock(DeploymentHandlerFactory.class);
        controlsFactory = mock(EquipmentControlsFactory.class);
        equipmentRegistry = mock(EquipmentRegistry.class);
        gameKey = GameKey.ofOpenMode();
        itemEffectFactory = mock(ItemEffectFactory.class);
        itemFactory = mock(ItemFactory.class);
        keyCreator = mock(NamespacedKeyCreator.class);
        particleEffectMapper = new ParticleEffectMapper();

        Plugin plugin = mock(Plugin.class);
        Mockito.when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, TEMPLATE_ID_KEY);

        keyCreator = mock(NamespacedKeyCreator.class);
        when(keyCreator.create(TEMPLATE_ID_KEY)).thenReturn(key);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void createReturnsEquipmentWithPlayerHolder() {
        EquipmentSpec spec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/frag_grenade.yml");
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(itemEffectFactory.create(spec.effect)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, controlsFactory, equipmentRegistry, itemEffectFactory, keyCreator, particleEffectMapper);
        Equipment equipment = factory.create(spec, gameKey, gamePlayer);

        assertThat(equipment).isInstanceOf(DefaultEquipment.class);
        assertThat(equipment.getId()).isEqualTo("FRAG_GRENADE");
        assertThat(equipment.getName()).isEqualTo("Frag Grenade");

        verify(equipmentRegistry).register(equipment, gamePlayer);
    }

    @Test
    public void createMakesEquipmentWithActivator() {
        EquipmentSpec spec = this.createEquipmentSpec("src/main/resources/items/lethal_equipment/c4.yml");

        ItemControls<EquipmentHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(spec), any(Equipment.class), eq(gameKey))).thenReturn(controls);

        ItemEffect itemEffect = mock(ItemEffect.class);
        when(itemEffectFactory.create(spec.effect)).thenReturn(itemEffect);

        DeploymentHandler deploymentHandler = mock(DeploymentHandler.class);
        when(deploymentHandlerFactory.create(any(DeploymentProperties.class), eq(itemEffect))).thenReturn(deploymentHandler);

        EquipmentFactory factory = new EquipmentFactory(deploymentHandlerFactory, controlsFactory, equipmentRegistry, itemEffectFactory, keyCreator, particleEffectMapper);
        Equipment equipment = factory.create(spec, gameKey);

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
