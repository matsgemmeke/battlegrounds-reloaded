package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.dejvokep.boostedyaml.YamlDocument;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.configuration.ResourceLoader;
import nl.matsgemmeke.battlegrounds.configuration.item.GunConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.configuration.item.spec.GunSpecification;
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
    private final EquipmentFactory equipmentFactory;
    @NotNull
    private final File itemsFolder;
    @NotNull
    private final FirearmFactory firearmFactory;
    @NotNull
    private final Logger logger;

    @Inject
    public WeaponCreatorProvider(
            @NotNull EquipmentFactory equipmentFactory,
            @NotNull FirearmFactory firearmFactory,
            @Named("ItemsFolder") @NotNull File itemsFolder,
            @NotNull Logger logger
    ) {
        this.equipmentFactory = equipmentFactory;
        this.firearmFactory = firearmFactory;
        this.itemsFolder = itemsFolder;
        this.logger = logger;
    }

    public WeaponCreator get() {
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs();
            this.copyResourcesFiles(itemsFolder);
        }

        WeaponCreator weaponCreator = new WeaponCreator();
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
        try {
            YamlDocument document = YamlDocument.create(itemFile);

            if (!document.contains("id")) {
                throw new InvalidItemConfigurationException("Cannot read item configuration file %s: Missing required 'id' value".formatted(itemFile.getName()));
            }

            this.addItemSpecification(creator, itemFile, document);
        } catch (IOException e) {
            logger.severe("Unable to load item configuration file %s".formatted(itemFile.getName()));
        } catch (InvalidItemConfigurationException e) {
            logger.severe(e.getMessage());
        }
    }

    private void addItemSpecification(@NotNull WeaponCreator creator, @NotNull File file, @NotNull YamlDocument document) {
        if (document.getString("gun-type") != null) {
            GunConfiguration configuration = new GunConfiguration(file, null);
            configuration.load();

            String id = document.getString("id");
            GunSpecification specification = configuration.createSpec();
            creator.addGunSpecification(id, specification);
        }
    }
}
