package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.dejvokep.boostedyaml.YamlDocument;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ResourceLoader;
import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.InvalidFieldSpecException;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.loader.*;
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

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
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
            logger.severe("Unable to load item configuration file '%s': %s".formatted(itemFile.getName(), e.getMessage()));
        } catch (InvalidFieldSpecException e) {
            logger.severe("An error occurred while loading item '%s': %s".formatted(id, e.getMessage()));
        }
    }

    private void addItemSpec(@NotNull WeaponCreator creator, @NotNull File file, @NotNull YamlDocument document) {
        String id = document.getString("id");

        if (document.getString("equipment-type") != null) {
            YamlReader yamlReader = new YamlReader(file, null);
            yamlReader.load();

            ItemConfiguration config = new ItemConfiguration(file, null);
            config.load();

            ActivationPatternSpecLoader activationPatternSpecLoader = new ActivationPatternSpecLoader(yamlReader);
            ParticleEffectSpecLoader particleEffectSpecLoader = new ParticleEffectSpecLoader(yamlReader);
            PotionEffectSpecLoader potionEffectSpecLoader = new PotionEffectSpecLoader(yamlReader);
            RangeProfileSpecLoader rangeProfileSpecLoader = new RangeProfileSpecLoader(yamlReader);
            TriggerSpecLoader triggerSpecLoader = new TriggerSpecLoader(yamlReader);

            ItemEffectSpecLoader itemEffectSpecLoader = new ItemEffectSpecLoader(yamlReader, activationPatternSpecLoader, particleEffectSpecLoader, potionEffectSpecLoader, rangeProfileSpecLoader, triggerSpecLoader);

            EquipmentSpecLoader specLoader = new EquipmentSpecLoader(yamlReader, itemEffectSpecLoader, particleEffectSpecLoader);
            EquipmentSpec spec = specLoader.loadSpec();

            creator.addEquipmentSpec(id, spec, config);
            return;
        }

        if (document.getString("gun-type") != null) {
            YamlReader yamlReader = new YamlReader(file, null);
            yamlReader.load();

            RangeProfileSpecLoader rangeProfileSpecLoader = new RangeProfileSpecLoader(yamlReader);

            GunSpecLoader specLoader = new GunSpecLoader(yamlReader, rangeProfileSpecLoader);
            GunSpec spec = specLoader.loadSpec();

            creator.addGunSpec(id, spec);
            return;
        }

        throw new IllegalStateException("File has no specified item type");
    }
}
