package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.ResourceLoader;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.configuration.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.GunFactory;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponFactory;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class WeaponCreatorProvider implements Provider<WeaponCreator> {

    private final File itemsFolder;
    private final Logger logger;
    private final Provider<EquipmentFactory> equipmentFactoryProvider;
    private final Provider<GunFactory> gunFactoryProvider;
    private final Provider<MeleeWeaponFactory> meleeWeaponFactoryProvider;
    private final SpecDeserializer specDeserializer;

    @Inject
    public WeaponCreatorProvider(
            Provider<EquipmentFactory> equipmentFactoryProvider,
            Provider<GunFactory> gunFactoryProvider,
            Provider<MeleeWeaponFactory> meleeWeaponFactoryProvider,
            SpecDeserializer specDeserializer,
            @Named("ItemsFolder") File itemsFolder,
            @Named("Battlegrounds") Logger logger
    ) {
        this.equipmentFactoryProvider = equipmentFactoryProvider;
        this.gunFactoryProvider = gunFactoryProvider;
        this.meleeWeaponFactoryProvider = meleeWeaponFactoryProvider;
        this.specDeserializer = specDeserializer;
        this.itemsFolder = itemsFolder;
        this.logger = logger;
    }

    @Override
    public WeaponCreator get() {
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs();
            this.copyResourcesFiles(itemsFolder);
        }

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider);
        File[] itemFolderFiles = itemsFolder.listFiles();

        if (itemFolderFiles == null || itemFolderFiles.length == 0) {
            String path = itemsFolder.getPath().replace('\\', '/');
            logger.warning("Unable to load item configuration files: directory '%s' is empty. To generate the default item files, delete the directory and reload the plugin.".formatted(path));
            return weaponCreator;
        }

        for (File itemFolderFile : itemFolderFiles) {
            if (itemFolderFile.isDirectory()) {
                File[] itemSubfolderFiles = itemFolderFile.listFiles();

                if (itemSubfolderFiles == null || itemSubfolderFiles.length == 0) {
                    String path = itemFolderFile.getPath().replace('\\', '/');
                    logger.warning("Unable to load item configuration files in items subfolder '%s'".formatted(path));
                    continue;
                }

                for (File itemSubfolderFile : itemSubfolderFiles) {
                    this.readItemFile(itemSubfolderFile, weaponCreator);
                }
            } else {
                this.readItemFile(itemFolderFile, weaponCreator);
            }
        }

        return weaponCreator;
    }

    private void copyResourcesFiles(File itemsDirectory) {
        URI uri = this.getResourcesURI();
        Path target = itemsDirectory.toPath();

        ResourceLoader loader = new ResourceLoader();

        try {
            loader.copyResource(uri, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private URI getResourcesURI() {
        try {
            return this.createResourceURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException();
        }
    }

    public URI createResourceURI() throws URISyntaxException {
        return this.getClass().getResource("/items").toURI();
    }

    private void readItemFile(File itemFile, WeaponCreator creator) {
        String id = null;

        try {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(itemFile);

            if ((id = configuration.getString("name")) == null) {
                logger.severe("An error occurred while loading file '%s': Identifier 'name' is missing".formatted(itemFile.getName()));
                return;
            }

            this.addItemSpec(creator, itemFile, configuration);
        } catch (IllegalArgumentException e) {
            logger.severe("Unable to load item configuration file '%s': %s".formatted(itemFile.getName(), e.getMessage()));
        } catch (ValidationException e) {
            logger.severe("An error occurred while loading item '%s': %s".formatted(id, e.getMessage()));
        }
    }

    private void addItemSpec(WeaponCreator creator, File file, YamlConfiguration configuration) {
        String name = configuration.getString("name");

        if (configuration.getString("equipment-type") != null) {
            EquipmentSpec equipmentSpec = specDeserializer.deserializeSpec(file, EquipmentSpec.class);
            ObjectValidator.validate(equipmentSpec);

            creator.addEquipmentSpec(name, equipmentSpec);
            return;
        }

        if (configuration.getString("gun-type") != null) {
            GunSpec gunSpec = specDeserializer.deserializeSpec(file, GunSpec.class);
            ObjectValidator.validate(gunSpec);

            creator.addGunSpec(name, gunSpec);
            return;
        }

        if (file.getParentFile().getName().endsWith("melee_weapons")) {
            MeleeWeaponSpec meleeWeaponSpec = specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
            ObjectValidator.validate(meleeWeaponSpec);

            creator.addMeleeWeaponSpec(name, meleeWeaponSpec);
            return;
        }

        logger.severe("An error occurred while loading item '%s': no item type is specified".formatted(name));
    }
}
