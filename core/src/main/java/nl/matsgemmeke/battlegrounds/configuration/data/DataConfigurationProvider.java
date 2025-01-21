package nl.matsgemmeke.battlegrounds.configuration.data;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DataConfigurationProvider implements Provider<DataConfiguration> {

    @NotNull
    private File dataFolder;

    @Inject
    public DataConfigurationProvider(@Named("DataFolder") @NotNull File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public DataConfiguration get() {
        File dataFile = new File(dataFolder.getAbsoluteFile(), "data.yml");

        DataConfiguration configuration = new DataConfiguration(dataFile);
        configuration.load();
        return configuration;
    }
}
