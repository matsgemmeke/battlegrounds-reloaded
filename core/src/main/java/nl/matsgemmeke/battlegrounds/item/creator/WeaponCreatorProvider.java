package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import dev.dejvokep.boostedyaml.YamlDocument;
import nl.matsgemmeke.battlegrounds.configuration.ResourceLoader;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.configuration.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class WeaponCreatorProvider implements Provider<WeaponCreator> {

    @NotNull
    private final File itemsFolder;
    @NotNull
    private final Logger logger;
    @NotNull
    private final Provider<EquipmentFactory> equipmentFactoryProvider;
    @NotNull
    private final Provider<FirearmFactory> gunFactoryProvider;
    @NotNull
    private final SpecDeserializer specDeserializer;

    @Inject
    public WeaponCreatorProvider(
            @NotNull Provider<EquipmentFactory> equipmentFactoryProvider,
            @NotNull Provider<FirearmFactory> gunFactoryProvider,
            @NotNull SpecDeserializer specDeserializer,
            @Named("ItemsFolder") @NotNull File itemsFolder,
            @Named("Battlegrounds") @NotNull Logger logger
    ) {
        this.equipmentFactoryProvider = equipmentFactoryProvider;
        this.gunFactoryProvider = gunFactoryProvider;
        this.specDeserializer = specDeserializer;
        this.itemsFolder = itemsFolder;
        this.logger = logger;
    }

    public WeaponCreator get() {
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs();
            this.copyResourcesFiles(itemsFolder);
        }

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
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

    private void copyResourcesFiles(@NotNull File itemsDirectory) {
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

    private void readItemFile(@NotNull File itemFile, @NotNull WeaponCreator creator) {
        String id = null;

        try {
            YamlDocument document = YamlDocument.create(itemFile);

            if ((id = document.getString("id")) == null) {
                logger.severe("An error occurred while loading file '%s': Identifier 'id' is missing".formatted(itemFile.getName()));
                return;
            }

            this.addItemSpec(creator, itemFile, document);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            logger.severe("Unable to load item configuration file '%s': %s".formatted(itemFile.getName(), e.getMessage()));
        } catch (ValidationException e) {
            e.printStackTrace();
            logger.severe("An error occurred while loading item '%s': %s".formatted(id, e.getMessage()));
        }
    }

    private void addItemSpec(@NotNull WeaponCreator creator, @NotNull File file, @NotNull YamlDocument document) {
        String id = document.getString("id");

        if (document.getString("equipment-type") != null) {
            EquipmentSpec equipmentSpec = specDeserializer.deserializeSpec(file, EquipmentSpec.class);
            ObjectValidator.validate(equipmentSpec);

            creator.addEquipmentSpec(id, equipmentSpec);
            return;
        }

        if (document.getString("gun-type") != null) {
            GunSpec gunSpec = specDeserializer.deserializeSpec(file, GunSpec.class);
            ObjectValidator.validate(gunSpec);

            creator.addGunSpec(id, gunSpec);
            return;
        }

        logger.severe("An error occurred while loading item '%s': no item type is specified".formatted(id));
    }
}
