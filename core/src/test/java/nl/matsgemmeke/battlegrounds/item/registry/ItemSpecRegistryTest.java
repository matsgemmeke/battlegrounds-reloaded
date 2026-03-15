package nl.matsgemmeke.battlegrounds.item.registry;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ItemSpecRegistryTest {

    private static final String EQUIPMENT_NAME = "Frag Grenade";
    private static final String GUN_NAME = "MP5";
    private static final String MELEE_WEAPON_NAME = "Combat Knife";

    private ItemSpecRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ItemSpecRegistry();
    }

    @ParameterizedTest
    @CsvSource({ "MP5,true", "Unknown,false" })
    @DisplayName("exists returns whether one of the specifications is registered by the given name")
    void exists_returnsWhetherNameExists(String itemName, boolean expectedExists) {
        GunSpec gunSpec = this.createGunSpec();

        registry.addGunSpec(GUN_NAME, gunSpec);
        boolean exists = registry.exists(itemName);

        assertThat(exists).isEqualTo(expectedExists);
    }

    @Test
    @DisplayName("getEquipmentSpec returns an optional containing the corresponding equipment specification")
    void getEquipmentSpec_nameExists() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();

        registry.addEquipmentSpec(EQUIPMENT_NAME, equipmentSpec);
        Optional<EquipmentSpec> equipmentSpecOptional = registry.getEquipmentSpec(EQUIPMENT_NAME);

        assertThat(equipmentSpecOptional).hasValue(equipmentSpec);
    }

    @Test
    @DisplayName("getEquipmentSpec returns an empty optional when no specification is found")
    void getEquipmentSpec_nameDoesNotExist() {
        Optional<EquipmentSpec> equipmentSpecOptional = registry.getEquipmentSpec(EQUIPMENT_NAME);

        assertThat(equipmentSpecOptional).isEmpty();
    }

    @Test
    @DisplayName("getGunSpec returns an optional containing the corresponding gun specification")
    void getGunSpec_nameExists() {
        GunSpec gunSpec = this.createGunSpec();

        registry.addGunSpec(GUN_NAME, gunSpec);
        Optional<GunSpec> gunSpecOptional = registry.getGunSpec(GUN_NAME);

        assertThat(gunSpecOptional).hasValue(gunSpec);
    }

    @Test
    @DisplayName("getGunSpec returns an empty optional when no specification is found")
    void getGunSpec_nameDoesNotExist() {
        Optional<GunSpec> gunSpecOptional = registry.getGunSpec(GUN_NAME);

        assertThat(gunSpecOptional).isEmpty();
    }

    @Test
    @DisplayName("getMeleeWeaponSpec returns an optional containing the corresponding melee weapon specification")
    void getMeleeWeaponSpec_nameExists() {
        MeleeWeaponSpec meleeWeaponSpec = this.createMeleeWeaponSpec();

        registry.addMeleeWeaponSpec(MELEE_WEAPON_NAME, meleeWeaponSpec);
        Optional<MeleeWeaponSpec> meleeWeaponSpecOptional = registry.getMeleeWeaponSpec(MELEE_WEAPON_NAME);

        assertThat(meleeWeaponSpecOptional).hasValue(meleeWeaponSpec);
    }

    @Test
    @DisplayName("getMeleeWeaponSpec returns an empty optional when no specification")
    void getMeleeWeaponSpec_nameDoesNotExist() {
        Optional<MeleeWeaponSpec> meleeWeaponSpecOptional = registry.getMeleeWeaponSpec(EQUIPMENT_NAME);

        assertThat(meleeWeaponSpecOptional).isEmpty();
    }

    private EquipmentSpec createEquipmentSpec() {
        File file = new File("src/main/resources/items/lethal_equipment/frag_grenade.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }

    private GunSpec createGunSpec() {
        File file = new File("src/main/resources/items/submachine_guns/mp5.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, GunSpec.class);
    }

    private MeleeWeaponSpec createMeleeWeaponSpec() {
        File file = new File("src/main/resources/items/melee_weapons/combat_knife.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
    }
}
