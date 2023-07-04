package com.github.matsgemmeke.battlegrounds.command;

import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import com.github.matsgemmeke.battlegrounds.configuration.GeneralDataConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMainLobbyCommand extends CommandSource {

    @NotNull
    private GeneralDataConfiguration generalData;
    @NotNull
    private Translator translator;

    public SetMainLobbyCommand(@NotNull GeneralDataConfiguration generalData, @NotNull Translator translator) {
        super("setmainlobby", translator.translate(TranslationKey.DESCRIPTION_SETMAINLOBBY.getPath()), "bg setmainlobby");
        this.generalData = generalData;
        this.translator = translator;
    }

    public void execute(@NotNull Player player) {
        // Get the center location of the block the player is standing on
        Location location = player.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

        generalData.setMainLobbyLocation(location);
        generalData.save();

        player.sendMessage(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath()));
    }
}
