package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSetupConfigurationFactory;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class CreateMapCommandExecutor {

    private final ArenaSetupConfigurationFactory arenaSetupConfigurationFactory;
    private final Translator translator;

    @Inject
    public CreateMapCommandExecutor(ArenaSetupConfigurationFactory arenaSetupConfigurationFactory, Translator translator) {
        this.arenaSetupConfigurationFactory = arenaSetupConfigurationFactory;
        this.translator = translator;
    }

    public void execute(CommandSender sender, int arenaId, String mapName) {
        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationFactory.create(arenaId);
        setupConfiguration.createMap(mapName);
        setupConfiguration.save();

        Map<String, Object> values = Map.of("bg_arena", arenaId, "bg_map", mapName);

        sender.sendMessage(translator.translate(TranslationKey.MAP_CREATED.getPath()).replace(values));
    }
}
