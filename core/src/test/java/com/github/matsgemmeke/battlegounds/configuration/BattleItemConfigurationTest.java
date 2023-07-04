package com.github.matsgemmeke.battlegounds.configuration;

import com.github.matsgemmeke.battlegrounds.configuration.BattleItemConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.*;

public class BattleItemConfigurationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File configFile;

    @Before
    public void setUp() throws IOException {
        this.configFile = folder.newFile("guns.yml");

        configFile.delete();
    }

    @Test
    public void canGetAllItemIds() throws IOException {
        File resourceFile = new File("src/main/resources/items/guns.yml");
        InputStream resource = new FileInputStream(resourceFile);

        BattleItemConfiguration configuration = new BattleItemConfiguration(configFile, resource);
        configuration.load();

        Set<String> idList = configuration.getIdList();

        assertTrue(idList.size() > 0);
    }
}
