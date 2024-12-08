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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class EquipmentFactoryTest {

    private AudioEmitter audioEmitter;
    private GameContext context;
    private ItemConfiguration configuration;
    private ItemEffectActivationFactory effectActivationFactory;
    private ItemEffectFactory effectFactory;
    private ItemFactory itemFactory;
    private ItemRegistry<Equipment, EquipmentHolder> equipmentRegistry;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private Section rootSection;
    private TaskRunner taskRunner;

    @BeforeEach
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
        when(keyCreator.create("battlegrounds-equipment")).thenReturn(key);

        rootSection = mock(Section.class);
        when(rootSection.getString("name")).thenReturn("name");
        when(rootSection.getString("description")).thenReturn("description");
        when(rootSection.getInt("item.damage")).thenReturn(1);
        when(rootSection.getString("item.material")).thenReturn("SHEARS");

        when(configuration.getRoot()).thenReturn(rootSection);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void shouldCreateSimpleEquipmentItem() {
        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertEquals("name", equipment.getName());
        assertEquals("description", equipment.getDescription());

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void createEquipmentItemWithDisplayName() {
        when(rootSection.getString("item.display-name")).thenReturn("&f%name%");

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertInstanceOf(DefaultEquipment.class, equipment);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingEquipmentItemWithInvalidMaterial() {
        when(rootSection.getString("item.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
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

        assertInstanceOf(DefaultEquipment.class, equipment);

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void throwExceptionWhenCreatingEquipmentItemWithInvalidActivatorMaterial() {
        Section activatorItemSection = mock(Section.class);
        when(activatorItemSection.getString("material")).thenReturn("fail");
        when(rootSection.getSection("item.activator")).thenReturn(activatorItemSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
    }

    @Test
    public void makeEquipmentItemWithEffectActivation() {
        Section effectSection = mock(Section.class);
        when(rootSection.getSection("effect")).thenReturn(effectSection);

        Section effectActivationSection = mock(Section.class);
        when(rootSection.getSection("effect.activation")).thenReturn(effectActivationSection);

        ItemEffect effect = mock(ItemEffect.class);
        when(effectFactory.make(effectSection, context)).thenReturn(effect);

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(effectActivationFactory.make(context, effect, effectActivationSection, null)).thenReturn(effectActivation);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertEquals(effectActivation, equipment.getEffectActivation());
    }

    @Test
    public void makeEquipmentItemWithBounceEffect() {
        Section bounceSection = mock(Section.class);

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.bounce")).thenReturn(bounceSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
    }

    @Test
    public void makeEquipmentItemWithSoundEffect() {
        Section soundSection = mock(Section.class);

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.sound")).thenReturn(soundSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
    }

    @Test
    public void makeEquipmentItemWithStickEffect() {
        Section stickSection = mock(Section.class);

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.stick")).thenReturn(stickSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
    }

    @Test
    public void makeEquipmentItemWithTrailEffect() {
        Section trailSection = mock(Section.class);
        when(trailSection.getString("particle.type")).thenReturn("FLAME");

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.trail")).thenReturn(trailSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getProjectileProperties());
        assertEquals(1, equipment.getProjectileProperties().getEffects().size());
    }

    @Test
    public void throwExceptionWhenCreatingEquipmentItemWithInvalidTrailEffectType() {
        Section trailSection = mock(Section.class);
        when(trailSection.getString("particle.type")).thenReturn("fail");

        Section projectileSection = mock(Section.class);
        when(projectileSection.getSection("effects.trail")).thenReturn(trailSection);

        when(rootSection.getSection("projectile")).thenReturn(projectileSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
    }

    @Test
    public void makeEquipmentItemWithThrowItemTemplate() {
        int damage = 1;

        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getInt("damage")).thenReturn(damage);
        when(throwItemSection.getString("material")).thenReturn("SHEARS");

        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertInstanceOf(DefaultEquipment.class, equipment);
        assertNotNull(equipment.getThrowItemTemplate());
        assertEquals(damage, equipment.getThrowItemTemplate().getDamage());

        verify(equipmentRegistry).registerItem(equipment);
    }

    @Test
    public void shouldThrowErrorWhenThrowItemMaterialConfigurationValueIsInvalid() {
        Section throwItemSection = mock(Section.class);
        when(throwItemSection.getString("material")).thenReturn("fail");

        when(rootSection.getSection("item.throw-item")).thenReturn(throwItemSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
    }

    @Test
    public void shouldCreateEquipmentItemWithThrowControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
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

        assertInstanceOf(DefaultEquipment.class, equipment);

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test
    public void shouldThrowErrorWhenThrowActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
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

        assertInstanceOf(DefaultEquipment.class, equipment);

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test
    public void shouldThrowErrorWhenCookActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("cook")).thenReturn("fail");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
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

        assertInstanceOf(DefaultEquipment.class, equipment);

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test
    public void shouldThrowErrorWhenPlaceActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("place")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
    }

    @Test
    public void shouldThrowErrorWhenPlacingMaterialConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("placing.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
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

        assertInstanceOf(DefaultEquipment.class, equipment);

        verify(equipmentRegistry).registerItem(equipment, gamePlayer);
    }

    @Test
    public void shouldThrowErrorWhenActivateActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("activate")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        assertThrows(CreateEquipmentException.class, () -> factory.make(configuration, context));
    }
}
