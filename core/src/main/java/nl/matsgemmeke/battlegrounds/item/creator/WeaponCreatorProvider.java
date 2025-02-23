package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ResourceLoader;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class WeaponCreatorProvider implements Provider<WeaponCreator> {

    @NotNull
    private EquipmentFactory equipmentFactory;
    @NotNull
    private File itemsFolder;
    @NotNull
    private FirearmFactory firearmFactory;

    @Inject
    public WeaponCreatorProvider(
            @NotNull EquipmentFactory equipmentFactory,
            @NotNull FirearmFactory firearmFactory,
            @Named("ItemsFolder") @NotNull File itemsFolder
    ) {
        this.equipmentFactory = equipmentFactory;
        this.firearmFactory = firearmFactory;
        this.itemsFolder = itemsFolder;
    }

    public WeaponCreator get() {
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs();
            this.copyResourcesFiles(itemsFolder);
        }

        WeaponCreator creator = new WeaponCreator();

        for (File itemTypeDirectory : itemsFolder.listFiles()) {
            for (File itemFile : itemTypeDirectory.listFiles()) {
                ItemConfiguration itemConfig = new ItemConfiguration(itemFile, null);
                itemConfig.load();

                this.addItemConfiguration(creator, itemConfig);
            }
        }

        return creator;
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

    private void addItemConfiguration(@NotNull WeaponCreator creator, @NotNull ItemConfiguration itemConfig) {
        WeaponFactory factory;

        if (itemConfig.getString("firearm-type") != null) {
            factory = firearmFactory;
        } else if (itemConfig.getString("equipment-type") != null) {
            factory = equipmentFactory;
        } else {
            return;
        }

        creator.addConfigurationFactory(itemConfig, factory);
    }
}
