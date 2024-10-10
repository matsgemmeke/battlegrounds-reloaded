package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.GeneralDataConfiguration;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMainLobbyCommand extends CommandSource {

    @NotNull
    private GeneralDataConfiguration generalData;
    @NotNull
    private Translator translator;

    public SetMainLobbyCommand(@NotNull GeneralDataConfiguration generalData, @NotNull Translator translator) {
        super("setmainlobby", translator.translate(TranslationKey.DESCRIPTION_SETMAINLOBBY.getPath()).getText(), "bg setmainlobby");
        this.generalData = generalData;
        this.translator = translator;
    }

    public void execute(@NotNull Player player) {
        // Get the center location of the block the player is standing on
        Location location = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

        generalData.setMainLobbyLocation(location);
        generalData.save();

        player.sendMessage(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath()).getText());
    }
}
