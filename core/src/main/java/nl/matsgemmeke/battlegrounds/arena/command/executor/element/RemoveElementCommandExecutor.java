package nl.matsgemmeke.battlegrounds.arena.command.executor.element;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.element.Element;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class RemoveElementCommandExecutor {

    private final ArenaMapSelector mapSelector;
    private final ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    private final Logger logger;
    private final Translator translator;

    @Inject
    public RemoveElementCommandExecutor(
            ArenaMapSelector mapSelector,
            ArenaSetupConfigurationProvider arenaSetupConfigurationProvider,
            @Named("Battlegrounds") Logger logger,
            Translator translator
    ) {
        this.mapSelector = mapSelector;
        this.arenaSetupConfigurationProvider = arenaSetupConfigurationProvider;
        this.logger = logger;
        this.translator = translator;
    }

    public void execute(Player player, int elementId) {
        UUID playerId = player.getUniqueId();
        ArenaMapSelection selection = mapSelector.getSelection(playerId).orElse(null);

        // Extra null check, the command should have already validated that the player has selected a map
        if (selection == null) {
            player.sendMessage(translator.translate(TranslationKey.NO_MAP_SELECTED.getPath()).getText());
            logger.severe("Player %s attempted to remove element %s without map selection despite prior validation".formatted(player.getName(), elementId));
            return;
        }

        int arenaId = selection.arena().getId();
        ArenaMap map = selection.map();
        String mapName = map.getName();
        Element element = map.getElement(elementId).orElse(null);

        // Extra null check, the command should have already validated the element id
        if (element == null) {
            Map<String, Object> values = Map.of(
                    "bg_map_name", mapName,
                    "bg_element_id", elementId
            );
            String message = translator.translate(TranslationKey.ELEMENT_NOT_EXISTS.getPath()).replace(values);

            player.sendMessage(message);
            logger.severe("Player %s attempted to remove element %s in map %s in arena %s, but the element id is invalid despite prior validation".formatted(player.getName(), elementId, mapName, arenaId));
            return;
        }

        map.removeElement(element);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationProvider.get(arenaId);
        setupConfiguration.removeElement(mapName, elementId);

        Map<String, Object> values = Map.of(
                "bg_map_name", mapName,
                "bg_element_id", elementId
        );
        String message = translator.translate(TranslationKey.ELEMENT_REMOVE_SUCCESSFUL.getPath()).replace(values);

        player.sendMessage(message);
    }
}
