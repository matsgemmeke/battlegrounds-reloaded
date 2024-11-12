package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.ItemRegistry;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivationFactory;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class EquipmentFactoryTest {

    private AudioEmitter audioEmitter;
    private GameContext context;
    private ItemConfiguration configuration;
    private ItemEffectActivationFactory effectActivationFactory;
    private ItemEffectFactory effectFactory;
    private ItemFactory itemFactory;
    private ItemRegistry<Equipment, EquipmentHolder> equipmentRegistry;
    private NamespacedKeyCreator keyCreator;
    private Section rootSection;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        configuration = mock(ItemConfiguration.class);
        effectActivationFactory = mock(ItemEffectActivationFactory.class);
        effectFactory = mock(ItemEffectFactory.class);
        itemFactory = mock(ItemFactory.class);
        equipmentRegistry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        keyCreator = mock(NamespacedKeyCreator.class);
        taskRunner = mock(TaskRunner.class);

        context = mock(GameContext.class);
        when(context.getAudioEmitter()).thenReturn(audioEmitter);
        when(context.getEquipmentRegistry()).thenReturn(equipmentRegistry);

        Plugin plugin = mock(Plugin.class);
        Mockito.when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, "battlegrounds-equipment");

        keyCreator = mock(NamespacedKeyCreator.class);
        Mockito.when(keyCreator.create("battlegrounds-equipment")).thenReturn(key);

        rootSection = mock(Section.class);
        when(rootSection.getString("name")).thenReturn("name");
        when(rootSection.getString("description")).thenReturn("description");
        when(rootSection.getInt("item.damage")).thenReturn(1);
        when(rootSection.getString("item.material")).thenReturn("SHEARS");

        when(configuration.getRoot()).thenReturn(rootSection);

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void shouldCreateSimpleEquipmentItem() {
        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertTrue(equipment instanceof DefaultEquipment);
        assertEquals("name", equipment.getName());
        assertEquals("description", equipment.getDescription());

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void createEquipmentItemWithDisplayName() {
        when(rootSection.getString("item.display-name")).thenReturn("&f%name%");

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertTrue(equipment instanceof DefaultEquipment);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowExceptionWhenCreatingEquipmentItemWithInvalidMaterial() {
        when(rootSection.getString("item.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithActivatorItem() {
        int damage = 1;
        String displayName = "&fActivator";

        Damageable itemMeta = mock(Damageable.class);
        when(itemFactory.getItemMeta(Material.FLINT)).thenReturn(itemMeta);

        Section activatorItemSection = mock(Section.class);
        when(activatorItemSection.getInt("damage")).thenReturn(damage);
        when(activatorItemSection.getString("display-name")).thenReturn(displayName);
        when(activatorItemSection.getString("material")).thenReturn("FLINT");

        when(rootSection.getSection("item.activator")).thenReturn(activatorItemSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertTrue(equipment instanceof DefaultEquipment);

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test(expected = CreateEquipmentException.class)
    public void throwExceptionWhenCreatingEquipmentItemWithInvalidActivatorMaterial() {
        Section activatorItemSection = mock(Section.class);
        when(activatorItemSection.getString("material")).thenReturn("fail");
        when(rootSection.getSection("item.activator")).thenReturn(activatorItemSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithThrowControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getInt("item.throw-item.damage")).thenReturn(1);
        when(rootSection.getString("item.throw-item.material")).thenReturn("FLINT");
        when(rootSection.getString("throwing.throw-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        Damageable itemMeta = mock(Damageable.class);
        ItemEffect effect = mock(ItemEffect.class);
        ItemEffectActivation activation = mock(ItemEffectActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(effectActivationFactory.make(eq(context), eq(effect), any(), any())).thenReturn(activation);
        when(effectFactory.make(any(), eq(context))).thenReturn(effect);
        when(itemFactory.getItemMeta(Material.FLINT)).thenReturn(itemMeta);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertTrue(equipment instanceof DefaultEquipment);

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenThrowActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        factory.make(configuration, context);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenThrowItemMaterialConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.throw-item.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithCookControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("cook")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.throw-item.material")).thenReturn("SHEARS");

        ItemEffect effect = mock(ItemEffect.class);
        ItemEffectActivation activation = mock(ItemEffectActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(effectActivationFactory.make(eq(context), eq(effect), any(), any())).thenReturn(activation);
        when(effectFactory.make(any(), eq(context))).thenReturn(effect);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertTrue(equipment instanceof DefaultEquipment);

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenCookActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("cook")).thenReturn("fail");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithPlaceControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("placing.material")).thenReturn("WARPED_BUTTON");

        ItemEffect effect = mock(ItemEffect.class);
        ItemEffectActivation activation = mock(ItemEffectActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(effectActivationFactory.make(eq(context), eq(effect), any(), any())).thenReturn(activation);
        when(effectFactory.make(any(), eq(context))).thenReturn(effect);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenPlaceActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("place")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        factory.make(configuration, context);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenPlacingMaterialConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("placing.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithActivateControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("activate")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        ItemEffect effect = mock(ItemEffect.class);
        ItemEffectActivation activation = mock(ItemEffectActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(effectActivationFactory.make(eq(context), eq(effect), any(), any())).thenReturn(activation);
        when(effectFactory.make(any(), eq(context))).thenReturn(effect);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenActivateActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("activate")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        factory.make(configuration, context);
    }
}
