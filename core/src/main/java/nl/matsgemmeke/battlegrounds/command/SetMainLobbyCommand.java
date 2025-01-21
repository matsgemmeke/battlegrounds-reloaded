package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.data.DataConfiguration;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMainLobbyCommand extends CommandSource {

    @NotNull
    private DataConfiguration dataConfiguration;
    @NotNull
    private Translator translator;

    @Inject
    public SetMainLobbyCommand(@NotNull DataConfiguration dataConfiguration, @NotNull Translator translator) {
        super("setmainlobby", translator.translate(TranslationKey.DESCRIPTION_SETMAINLOBBY.getPath()).getText(), "bg setmainlobby");
        this.dataConfiguration = dataConfiguration;
        this.translator = translator;
    }

    public void execute(@NotNull Player player) {
        // Get the center location of the block the player is standing on
        Location location = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

        dataConfiguration.setMainLobbyLocation(location);
        dataConfiguration.save();

        player.sendMessage(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath()).getText());
    }
}
