package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ResourceLoader;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class WeaponProviderLoader {

    @NotNull
    private EquipmentFactory equipmentFactory;
    @NotNull
    private FirearmFactory firearmFactory;

    public WeaponProviderLoader(
            @NotNull EquipmentFactory equipmentFactory,
            @NotNull FirearmFactory firearmFactory
    ) {
        this.equipmentFactory = equipmentFactory;
        this.firearmFactory = firearmFactory;
    }

    public WeaponProvider loadWeaponProvider(@NotNull File itemsDirectory) throws IOException, IllegalStateException {
        if (!itemsDirectory.exists()) {
            itemsDirectory.mkdirs();
            this.copyResourcesFiles(itemsDirectory);
        }

        WeaponProvider provider = new WeaponProvider();

        for (File itemTypeDirectory : itemsDirectory.listFiles()) {
            for (File itemFile : itemTypeDirectory.listFiles()) {
                ItemConfiguration itemConfig = new ItemConfiguration(itemFile, null);
                itemConfig.load();

                this.addItemConfiguration(provider, itemConfig);
            }
        }

        return provider;
    }

    private void copyResourcesFiles(@NotNull File itemsDirectory) throws IOException, IllegalStateException {
        URI uri = this.getResourcesURI();
        Path target = itemsDirectory.toPath();

        ResourceLoader loader = new ResourceLoader();
        loader.copyResource(uri, target);
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

    private void addItemConfiguration(@NotNull WeaponProvider provider, @NotNull ItemConfiguration itemConfig) {
        WeaponFactory factory;

        if (itemConfig.getString("firearm-type") != null) {
            factory = firearmFactory;
        } else if (itemConfig.getString("equipment-type") != null) {
            factory = equipmentFactory;
        } else {
            return;
        }

        provider.addConfigurationFactory(itemConfig, factory);
    }
}
