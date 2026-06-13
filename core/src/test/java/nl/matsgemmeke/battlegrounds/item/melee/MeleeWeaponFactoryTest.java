package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.melee.controls.MeleeWeaponControllerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandler;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandlerFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponFactoryTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_SWORD);

    @Mock
    private ItemTemplateFactory itemTemplateFactory;
    @Mock
    private MeleeWeaponControllerFactory controllerFactory;
    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
    @Mock
    private ReloadSystemFactory reloadSystemFactory;
    @Mock
    private ThrowHandlerFactory throwHandlerFactory;
    @InjectMocks
    private MeleeWeaponFactory meleeWeaponFactory;

    @Test
    @DisplayName("create returns MeleeWeapon without assigned user")
    void create_withoutUser() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("combat_knife");

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(any())).thenReturn(ITEM_STACK);

        when(itemTemplateFactory.create(spec.item)).thenReturn(itemTemplate);

        MeleeWeapon result = meleeWeaponFactory.create(spec);

        assertThat(result).isInstanceOfSatisfying(DefaultMeleeWeapon.class, meleeWeapon -> {
            assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
            assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
            assertThat(meleeWeapon.getUser()).isEmpty();
            assertThat(meleeWeapon.getItemStack()).isEqualTo(ITEM_STACK);
            assertThat(meleeWeapon.getAttackDamage()).isEqualTo(75.0);
            assertThat(meleeWeapon.getReloadSystem()).isNull();
            assertThat(meleeWeapon.getResourceContainer()).satisfies(resourceContainer -> {
               assertThat(resourceContainer.getCapacity()).isEqualTo(1);
               assertThat(resourceContainer.getLoadedAmount()).isEqualTo(1);
               assertThat(resourceContainer.getReserveAmount()).isZero();
               assertThat(resourceContainer.getMaxReserveAmount()).isZero();
            });
        });

        verify(meleeWeaponRegistry).register(result);
    }

    @Test
    @DisplayName("create returns MeleeWeapon with controls")
    void create_withControls() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("ballistic_knife");

        MeleeWeapon result = meleeWeaponFactory.create(spec);

        assertThat(result).isInstanceOfSatisfying(DefaultMeleeWeapon.class, meleeWeapon -> {
            assertThat(meleeWeapon.getName()).isEqualTo("Ballistic Knife");
            assertThat(meleeWeapon.getDescription()).isEqualTo("Spring-action knife launcher. Can fire the blade as a projectile.");
            assertThat(meleeWeapon.getUser()).isEmpty();
            assertThat(meleeWeapon.getAttackDamage()).isEqualTo(60.0);
            assertThat(meleeWeapon.getReloadSystem()).isNull();
        });

        verify(controllerFactory).create(eq(spec.controls), any(MeleeWeapon.class));
        verify(meleeWeaponRegistry).register(result);
    }

    @Test
    @DisplayName("create returns MeleeWeapon with reloading")
    void create_withReloading() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("ballistic_knife");
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        ThrowHandler throwHandler = mock(ThrowHandler.class);

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);
        when(throwHandlerFactory.create(eq(spec.throwing), any(ItemRepresentation.class), any(ResourceContainer.class), eq("Ballistic Knife"))).thenReturn(throwHandler);

        MeleeWeapon result = meleeWeaponFactory.create(spec);

        assertThat(result).isInstanceOfSatisfying(DefaultMeleeWeapon.class, meleeWeapon -> {
            assertThat(meleeWeapon.getName()).isEqualTo("Ballistic Knife");
            assertThat(meleeWeapon.getDescription()).isEqualTo("Spring-action knife launcher. Can fire the blade as a projectile.");
            assertThat(meleeWeapon.getUser()).isEmpty();
            assertThat(meleeWeapon.getAttackDamage()).isEqualTo(60.0);
            assertThat(meleeWeapon.getReloadSystem()).isEqualTo(reloadSystem);
        });

        verify(meleeWeaponRegistry).register(result);
    }

    @Test
    @DisplayName("create returns MeleeWeapon with throwing")
    void create_withThrowing() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("tomahawk");
        ThrowHandler throwHandler = mock(ThrowHandler.class);

        when(throwHandlerFactory.create(eq(spec.throwing), any(ItemRepresentation.class), any(ResourceContainer.class), eq("Tomahawk"))).thenReturn(throwHandler);

        MeleeWeapon result = meleeWeaponFactory.create(spec);

        assertThat(result).isInstanceOfSatisfying(DefaultMeleeWeapon.class, meleeWeapon -> {
            assertThat(meleeWeapon.getName()).isEqualTo("Tomahawk");
            assertThat(meleeWeapon.getDescription()).isEqualTo("Retrievable throwing hatchet that causes instant death on impact.");
            assertThat(meleeWeapon.getUser()).isEmpty();
            assertThat(meleeWeapon.getAttackDamage()).isEqualTo(50.0);
            assertThat(meleeWeapon.getReloadSystem()).isNull();
        });

        verify(meleeWeaponRegistry).register(result);
    }

    private MeleeWeaponSpec createMeleeWeaponSpec(String fileName) {
        File file = new File("src/main/resources/items/melee_weapons/" + fileName + ".yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
    }
}
